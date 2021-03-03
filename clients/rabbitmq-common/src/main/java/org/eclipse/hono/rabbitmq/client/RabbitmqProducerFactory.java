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

import java.util.Map;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 * A factory for creating Kafka producers.
 * <p>
 * Vert.x expects producers to be closed when they are no longer needed to close all open connections and release its
 * resources. Producers are expected to be closed before the application shuts down (even if this is done automatically
 * for producers that are created from inside of verticles).
 */
public interface RabbitmqProducerFactory {

    /**
     * Creates a new factory that produces {@link RabbitmqProducer#createShared(Vertx, String, Map) shared producers}.
     * Shared producers can safely be shared between verticle instances.
     * <p>
     * Config must always be the same for the same key in {@link #getOrCreateProducer(String, Map)}.
     * <p>
     * The resources of a shared producer are released when the last producer with a given name is closed.
     *
     * @param vertx The Vert.x instance to use.
     * @return An instance of the factory.
     */
    static RabbitmqProducerFactory sharedProducerFactory(final Vertx vertx) {
        return new CachingRabbitmqProducerFactory((name, config) -> RabbitMQClient.create(vertx, config));
    }

    /**
     * Gets a producer for sending data to Rabbitmq.
     * <p>
     * The producer returned may be either newly created or it may be an existing producer for the given producer name.
     * The config parameter might be ignored if an existing producer is returned.
     * <p>
     * Do not hold references to the returned producer between send operations, because the producer might be closed by
     * the factory. Instead, always get an instance by invoking this method.
     * <p>
     * When the producer is no longer needed, it should be closed to release the resources (like connections). NB: Do
     * not close the returned producer directly. Instead, call {@link #closeProducer(String)} to let the factory close
     * the producer and update its internal state.
     *
     * @param producerName The name to identify the producer.
     * @param config       The Rabbitmq configuration with which the producer is to be created.
     * @return an existing or new producer.
     */
    RabbitMQClient getOrCreateProducer(String producerName, RabbitMQOptions config);

    /**
     * Closes the producer with the given producer name if it exists.
     * <p>
     * This method is expected to be invoked as soon as the producer is no longer needed, especially before the
     * application shuts down.
     *
     * @param producerName The name of the producer to remove.
     * @return A future that is completed when the close operation completed or a succeeded future if no producer
     * existed with the given name.
     */
    Future<Void> closeProducer(String producerName);

}
