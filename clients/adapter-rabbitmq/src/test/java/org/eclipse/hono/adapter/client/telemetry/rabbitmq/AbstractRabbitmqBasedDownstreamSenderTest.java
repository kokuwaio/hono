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

package org.eclipse.hono.adapter.client.telemetry.rabbitmq;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.hono.config.ProtocolAdapterProperties;
import org.eclipse.hono.rabbitmq.client.CachingRabbitmqProducerFactory;
import org.eclipse.hono.util.QoS;
import org.eclipse.hono.util.RegistrationAssertion;
import org.eclipse.hono.util.TenantObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 * Verifies behavior of {@link AbstractRabbitmqBasedDownstreamSender}.
 */
@ExtendWith(VertxExtension.class)
public class AbstractRabbitmqBasedDownstreamSenderTest {

    private static final String TENANT_ID = "the-tenant";
    private static final String DEVICE_ID = "the-device";
    private static final QoS qos = QoS.AT_LEAST_ONCE;
    private static final String CONTENT_TYPE = "the-content-type";
    private static final String PRODUCER_NAME = "test-producer";
    private static final String EXCHANGE_NAME = "test";
    private static final String QUEUE_NAME = "test-rico";
    private static final String RABBITMQ_IMAGE_NAME = System.getProperty("mongoDbImageName", "rabbitmq:3.8-management");
    public static RabbitMQContainer RABBITMQ_CONTAINER = new RabbitMQContainer(
            DockerImageName.parse(RABBITMQ_IMAGE_NAME))
                    // .withExposedPorts(5672, 15672)
                    .withVhost("/")
                    .withUser("admin", "admin")
                    // .withExchange(EVENT_EXCHANGE_NAME, "fanout")
                    // .withExchange(TELEMETRY_EXCHANGE_NAME, "fanout")
                    // .withExchange(COMMAND_EXCHANGE_NAME, "fanout")
                    .withPermission("/", "admin", ".*", ".*", ".*");

    protected final Tracer tracer = NoopTracerFactory.create();
    private final RabbitMQOptions config = new RabbitMQOptions();
    private final TenantObject tenant = new TenantObject(TENANT_ID, true);
    private final RegistrationAssertion device = new RegistrationAssertion(DEVICE_ID);
    private final ProtocolAdapterProperties adapterConfig = new ProtocolAdapterProperties();

    /**
     * Sets up docker container for tests.
     */
    @BeforeAll
    public static void allSetUp() {
        RABBITMQ_CONTAINER.start();
    }

    /**
     * Sets up the fixture.
     */
    @BeforeEach
    public void setUp() {
        this.config.setHost(RABBITMQ_CONTAINER.getHost());
        this.config.setPort(RABBITMQ_CONTAINER.getAmqpPort());
        this.config.setUser(RABBITMQ_CONTAINER.getAdminUsername());
        this.config.setPassword(RABBITMQ_CONTAINER.getAdminPassword());

    }

    /**
     * Verifies that {@link AbstractRabbitmqBasedDownstreamSender#start()} creates a producer and
     * {@link AbstractRabbitmqBasedDownstreamSender#stop()} closes it.
     */
    @Test
    public void testLifecycle() {
        final CachingRabbitmqProducerFactory factory = TestHelper.newProducerFactory();
        final AbstractRabbitmqBasedDownstreamSender sender = newSender(factory);

        assertThat(factory.getProducer(PRODUCER_NAME)).isEmpty();
        sender.start();
        assertThat(factory.getProducer(PRODUCER_NAME)).isNotEmpty();
        sender.stop();
        assertThat(factory.getProducer(PRODUCER_NAME)).isEmpty();
    }

    /**
     * Verifies that the Kafka record is created as expected.
     *
     * @param ctx The vert.x test context.
     * @throws InterruptedException if the test execution gets interrupted
     * @throws ExecutionException if the test execution fails
     * @throws TimeoutException if the test execution times out
     */
    @Test
    public void testSendCreatesCorrectRecord(final VertxTestContext ctx)
            throws InterruptedException, TimeoutException, ExecutionException {

        // GIVEN a sender
        final String payload = "the-payload";
        final Map<String, Object> properties = Collections.singletonMap("foo", "bar");
        final AbstractRabbitmqBasedDownstreamSender sender = newSender(TestHelper.newProducerFactory());

        final RabbitMQClient testClient = createTestClient(config);

        final CompletableFuture<Void> latch = new CompletableFuture<>();
        testClient.start(r -> latch.complete(null));
        latch.get(10L, TimeUnit.SECONDS);
        System.out.println(testClient.isConnected());

        createExchange(testClient, EXCHANGE_NAME);
        createQueue(testClient, QUEUE_NAME);
        bindQueue(testClient, EXCHANGE_NAME, QUEUE_NAME);

        final QueueOptions queueOptions = new QueueOptions();
        queueOptions.setAutoAck(true);
        testClient.basicConsumer(QUEUE_NAME, queueOptions, (h) -> {
            if (h.succeeded()) {
                System.out.println("RabbitMQ consumer created !");
                final RabbitMQConsumer mqConsumer = h.result();
                mqConsumer.handler(message -> {
                    System.out.println("Got message: " + message.body().toString());
                });
                mqConsumer.endHandler(v -> {
                    System.out.println("It is the end of the stream");
                });
                mqConsumer.exceptionHandler(e -> {
                    System.out.println("An exception occurred in the process of message handling");
                    e.printStackTrace();
                });
            } else {
                h.cause().printStackTrace();
            }
        });

        sender.start();
        // WHEN sending a message
        sender.send(EXCHANGE_NAME, tenant, device, qos, CONTENT_TYPE, Buffer.buffer(payload), properties, null)
                .onComplete(ctx.succeeding(v -> {
                    System.out.println("test");
                }));
        Thread.sleep(5000);

        ctx.completeNow();
    }

    private AbstractRabbitmqBasedDownstreamSender newSender(final CachingRabbitmqProducerFactory factory) {
        return new AbstractRabbitmqBasedDownstreamSender(factory, PRODUCER_NAME, config, adapterConfig, tracer) {
        };
    }

    private RabbitMQClient createTestClient(final RabbitMQOptions config) {
        return RabbitMQClient.create(Vertx.vertx(), config);
    }

    private void createQueue(final RabbitMQClient client, final String name) throws InterruptedException {
        final JsonObject config = new JsonObject();
        final CountDownLatch l = new CountDownLatch(1);
        client.queueDeclare(name, true, false, true, config, queueResult -> {
            if (queueResult.succeeded()) {
                System.out.println("Queue declared!");
            } else {
                System.out.println("Queue failed to be declared!");
                queueResult.cause().printStackTrace();
            }
            l.countDown();
        });

        l.await(1, TimeUnit.SECONDS);
    }

    private void createExchange(final RabbitMQClient client, final String name) throws InterruptedException {
        final JsonObject config = new JsonObject();
        final CountDownLatch l = new CountDownLatch(1);
        client.exchangeDeclare(name, "fanout", true, false, config, queueResult -> {
            if (queueResult.succeeded()) {
                System.out.println("Exchange declared! " + name);
            } else {
                System.out.println("Exchange failed to be declared!");
                queueResult.cause().printStackTrace();
            }
            l.countDown();
        });

        l.await(1, TimeUnit.SECONDS);
    }

    private void bindQueue(final RabbitMQClient client, final String exchange, final String queue)
            throws InterruptedException {
        final CountDownLatch l = new CountDownLatch(1);
        client.queueBind(queue, exchange, "#", queueResult -> {
            if (queueResult.succeeded()) {
                System.out.println("Bind declared!");
            } else {
                System.out.println("Bind failed to be declared!");
                queueResult.cause().printStackTrace();
            }
            l.countDown();
        });

        l.await(1, TimeUnit.SECONDS);
    }

}
