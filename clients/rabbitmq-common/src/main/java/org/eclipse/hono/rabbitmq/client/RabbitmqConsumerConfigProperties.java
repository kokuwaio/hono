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

import java.util.Objects;

import io.vertx.rabbitmq.RabbitMQOptions;

/**
 * Dummy.
 */
public class RabbitmqConsumerConfigProperties {

    private RabbitMQOptions consumerConfig;

    /**
     * @param consumerConfig consumerConfig
     */
    public void setConsumerConfig(final RabbitMQOptions consumerConfig) {
        this.consumerConfig = Objects.requireNonNull(consumerConfig);

    }

    /**
     * @return An instance of rabbitmq config options.
     */
    public RabbitMQOptions getConsumerConfig() {
        return consumerConfig;
    }
}
