/*
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.hono.rabbitmq.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import org.eclipse.hono.test.VertxMockSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 * Verifies behavior of {@link CachingRabbitmqProducerFactory}.
 */
public class CachingRabbitmqProducerFactoryTest {

    private static final String PRODUCER_NAME = "test-producer";
    private static final String RABBITMQ_IMAGE_NAME = System.getProperty("mongoDbImageName", "rabbitmq:3.8-management");

    private static final RabbitMQContainer RABBITMQ_CONTAINER = new RabbitMQContainer(
            DockerImageName.parse(RABBITMQ_IMAGE_NAME))
                    // .withExposedPorts(5672, 15672)
                    .withVhost("/")
                    .withUser("admin", "admin")
                    // .withExchange(EVENT_EXCHANGE_NAME, "fanout")
                    // .withExchange(TELEMETRY_EXCHANGE_NAME, "fanout")
                    // .withExchange(COMMAND_EXCHANGE_NAME, "fanout")
                    .withPermission("/", "admin", ".*", ".*", ".*");

    private final RabbitMQOptions config = new RabbitMQOptions();

    private CachingRabbitmqProducerFactory factory;

    /**
     * Sets up docker container for tests.
     */
    @BeforeAll
    public static void allSetUp() {
        // setProperty(LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory.class.getName());
        RABBITMQ_CONTAINER.start();
    }

    /**
     * Sets up the fixture.
     */
    @BeforeEach
    void setUp() {

        this.config.setHost(RABBITMQ_CONTAINER.getHost());
        this.config.setPort(RABBITMQ_CONTAINER.getAmqpPort());
        this.config.setUser(RABBITMQ_CONTAINER.getAdminUsername());
        this.config.setPassword(RABBITMQ_CONTAINER.getAdminPassword());

        final Vertx vertxMock = mock(Vertx.class);
        final Context context = VertxMockSupport.mockContext(vertxMock);
        when(vertxMock.getOrCreateContext()).thenReturn(context);

        final BiFunction<String, RabbitMQOptions, RabbitMQClient> instanceSupplier = (n, c) -> RabbitMQClient
                .create(Vertx.vertx(), config);

        factory = new CachingRabbitmqProducerFactory(instanceSupplier);
    }

    /**
     * Verifies that getOrCreateProducer() creates a producers and adds it to the cache.
     */
    @Test
    public void testThatProducerIsAddedToCache() {

        assertThat(factory.getProducer(PRODUCER_NAME)).isEmpty();

        final RabbitMQClient createProducer = factory.getOrCreateProducer(PRODUCER_NAME, config);

        final Optional<RabbitMQClient> actual = factory.getProducer(PRODUCER_NAME);
        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).isEqualTo(createProducer);
    }

    /**
     * Verifies that {@link CachingRabbitmqProducerFactory#closeProducer(String)} closes the producer and removes it
     * from the cache.
     *
     * @throws InterruptedException if the test execution gets interrupted
     * @throws ExecutionException if the test execution fails
     * @throws TimeoutException if the test execution times out
     */
    @Test
    public void testRemoveProducerClosesAndRemovesFromCache()
            throws InterruptedException, ExecutionException, TimeoutException {
        final String producerName1 = "first-producer";
        final String producerName2 = "second-producer";

        // GIVEN a factory that contains two producers
        final RabbitMQClient producer1 = factory.getOrCreateProducer(producerName1, config);
        final RabbitMQClient producer2 = factory.getOrCreateProducer(producerName2, config);

        assertThat(producer1.isConnected()).isTrue();
        assertThat(producer2.isConnected()).isTrue();
        // WHEN removing one producer
        final CompletableFuture<Void> latch = new CompletableFuture<>();
        factory.closeProducer(producerName1).setHandler((a) -> latch.complete(null));
        latch.get(2L, TimeUnit.SECONDS);

        // THEN the producer is closed...
        assertThat(producer1.isConnected()).isFalse();
        // ...AND removed from the cache
        assertThat(factory.getProducer(producerName1)).isEmpty();
        // ...AND the second producers is still present and open
        assertThat(factory.getProducer(producerName2)).isNotEmpty();
        assertThat(producer2.isConnected()).isTrue();
    }

}
