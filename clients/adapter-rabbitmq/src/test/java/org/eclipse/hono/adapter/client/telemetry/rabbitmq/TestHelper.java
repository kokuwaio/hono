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

import org.eclipse.hono.rabbitmq.client.CachingRabbitmqProducerFactory;

import io.vertx.core.Vertx;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * A helper class for writing tests with the Rabbitmq client.
 */
public class TestHelper {

    private TestHelper() {
    }

    /**
     * Returns a new {@link org.eclipse.hono.client.impl.CachingClientFactory}.
     * <p>
     * Dummy
     *
     * @return The producer factory.
     */
    public static CachingRabbitmqProducerFactory newProducerFactory() {
        return new CachingRabbitmqProducerFactory((n, c) -> RabbitMQClient.create(Vertx.vertx(), c));
    }

}
