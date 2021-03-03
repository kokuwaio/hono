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
public class RabbitmqProducerConfigProperties {

    private RabbitMQOptions producerConfig;
    private boolean exchangePerTenant = false;
    private String eventExchange;
    private String telemetryExchange;
    private String eventExchangePrefix;
    private String telemetryExchangePrefix;

    /**
     * @return An instance of rabbitmq config options.
     */
    public RabbitMQOptions getProducerConfig() {
        return this.producerConfig;
    }

    /**
     * @param producerConfig producerConfig
     */
    public void setProducerConfig(final RabbitMQOptions producerConfig) {
        this.producerConfig = Objects.requireNonNull(producerConfig);
    }

    public boolean isExchangePerTenant() {
        return exchangePerTenant;
    }

    public void setExchangePerTenant(final boolean exchangePerTenant) {
        this.exchangePerTenant = exchangePerTenant;
    }

    public String getEventExchange() {
        return eventExchange;
    }

    public void setEventExchange(final String eventExchange) {
        this.eventExchange = eventExchange;
    }

    public String getTelemetryExchange() {
        return telemetryExchange;
    }

    public void setTelemetryExchange(final String telemetryExchange) {
        this.telemetryExchange = telemetryExchange;
    }

    public String getEventExchangePrefix() {
        return eventExchangePrefix;
    }

    public void setEventExchangePrefix(final String eventExchangePrefix) {
        this.eventExchangePrefix = eventExchangePrefix;
    }

    public String getTelemetryExchangePrefix() {
        return telemetryExchangePrefix;
    }

    public void setTelemetryExchangePrefix(final String telemetryExchangePrefix) {
        this.telemetryExchangePrefix = telemetryExchangePrefix;
    }
}
