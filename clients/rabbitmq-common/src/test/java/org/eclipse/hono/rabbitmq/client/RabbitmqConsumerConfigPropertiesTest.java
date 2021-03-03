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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.vertx.rabbitmq.RabbitMQOptions;

/**
 * Verifies the behavior of {@link RabbitmqConsumerConfigProperties}.
 */
public class RabbitmqConsumerConfigPropertiesTest {

    /**
     * Verifies that trying to set a {@code null} config throws a Nullpointer exception.
     */
    @Test
    public void testThatConfigCanNotBeSetToNull() {
        assertThrows(NullPointerException.class, () -> new RabbitmqConsumerConfigProperties().setConsumerConfig(null));
    }

    /**
     * Verifies that properties provided with
     * {@link RabbitmqConsumerConfigProperties#setConsumerConfig(io.vertx.rabbitmq.RabbitMQOptions)} are returned in
     * {@link RabbitmqConsumerConfigProperties#getConsumerConfig()}.
     */
    @Test
    public void testThatGetConsumerConfigReturnsGivenProperties() {
        final RabbitmqConsumerConfigProperties config = new RabbitmqConsumerConfigProperties();
        config.setConsumerConfig(new RabbitMQOptions());

    }

}
