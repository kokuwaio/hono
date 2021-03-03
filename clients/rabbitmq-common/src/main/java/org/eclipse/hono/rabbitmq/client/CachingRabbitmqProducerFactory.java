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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 * A factory for creating Rabbitmq producers. Created producers are being cached.
 * <p>
 * This implementation provides no synchronization and should not be used by multiple threads. To create producers that
 * can safely be shared between verticle instances, use {@link RabbitmqProducerFactory#sharedProducerFactory(Vertx)}.
 * <p>
 */
public class CachingRabbitmqProducerFactory implements RabbitmqProducerFactory {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, RabbitMQClient> activeProducers = new HashMap<>();
    private final BiFunction<String, RabbitMQOptions, RabbitMQClient> producerInstanceSupplier;

    /**
     * Creates a new producer factory.
     * <p>
     * Use {@link RabbitmqProducerFactory#sharedProducerFactory(Vertx)} to create producers that can safely be shared
     * between verticle instances.
     *
     * @param producerInstanceSupplier The function that provides new producer instances.
     */
    public CachingRabbitmqProducerFactory(
            final BiFunction<String, RabbitMQOptions, RabbitMQClient> producerInstanceSupplier) {
        this.producerInstanceSupplier = producerInstanceSupplier;
    }

    /**
     * Gets a producer for sending data to Rabbitmq.
     * <p>
     * This method first tries to look up an already existing producer using the given name. If no producer exists yet,
     * a new instance is created using the given factory and put to the cache.
     * <p>
     * The given config is ignored when an existing producers is returned.
     *
     * @param producerName The name to identify the producer.
     * @param config The Rabbitmq configuration with which the producer is to be created.
     * @return an existing or new producer.
     */
    @Override
    public RabbitMQClient getOrCreateProducer(final String producerName, final RabbitMQOptions config) {

        activeProducers.computeIfAbsent(producerName, (name) -> {
            final RabbitMQClient producer = producerInstanceSupplier.apply(producerName, config);
            final CompletableFuture<Void> latch = new CompletableFuture<>();
            producer.start(r -> latch.complete(null));
            try {
                latch.get(10L, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(e.getMessage(), e);
            }
            return producer;
        });

        return activeProducers.get(producerName);
    }

    /**
     * Gets an existing producer.
     *
     * @param producerName The name to look up the producer.
     * @return The producer or {@code null} if the cache does not contain the name.
     */
    public Optional<RabbitMQClient> getProducer(final String producerName) {
        return Optional.ofNullable(activeProducers.get(producerName));
    }

    /**
     * {@inheritDoc}
     * <p>
     * If a producer with the given name exists, it is removed from the cache.
     */
    @Override
    public Future<Void> closeProducer(final String producerName) {
        final RabbitMQClient producer = activeProducers.remove(producerName);
        if (producer == null) {
            return Future.succeededFuture();
        } else {
            final Promise<Void> promise = Promise.promise();
            producer.stop(promise);
            return promise.future();
        }
    }
}
