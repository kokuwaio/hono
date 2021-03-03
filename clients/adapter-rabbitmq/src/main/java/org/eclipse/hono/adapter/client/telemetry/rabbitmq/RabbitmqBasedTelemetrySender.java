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

import java.util.Map;
import java.util.Objects;

import org.eclipse.hono.adapter.client.telemetry.TelemetrySender;
import org.eclipse.hono.config.ProtocolAdapterProperties;
import org.eclipse.hono.rabbitmq.client.RabbitmqProducerConfigProperties;
import org.eclipse.hono.rabbitmq.client.RabbitmqProducerFactory;
import org.eclipse.hono.util.QoS;
import org.eclipse.hono.util.RegistrationAssertion;
import org.eclipse.hono.util.TelemetryConstants;
import org.eclipse.hono.util.TenantObject;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;

/**
 * A client for publishing telemetry messages to a Kafka cluster.
 */
public class RabbitmqBasedTelemetrySender extends AbstractRabbitmqBasedDownstreamSender implements TelemetrySender {

    private final RabbitmqProducerConfigProperties rabbitmqProducerConfig;

    /**
     * Creates a new Kafka-based telemetry sender.
     *
     * @param producerFactory The factory to use for creating Kafka producers.
     * @param rabbitmqProducerConfig The Kafka producer configuration properties to use.
     * @param adapterConfig The protocol adapter's configuration properties.
     * @param tracer The OpenTracing tracer.
     * @throws NullPointerException if any of the parameters are {@code null}.
     */
    public RabbitmqBasedTelemetrySender(final RabbitmqProducerFactory producerFactory,
            final RabbitmqProducerConfigProperties rabbitmqProducerConfig,
            final ProtocolAdapterProperties adapterConfig,
            final Tracer tracer) {

        super(producerFactory, TelemetryConstants.TELEMETRY_ENDPOINT, rabbitmqProducerConfig.getProducerConfig(),
                adapterConfig, tracer);
        this.rabbitmqProducerConfig = rabbitmqProducerConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<Void> sendTelemetry(final TenantObject tenant, final RegistrationAssertion device, final QoS qos,
            final String contentType, final Buffer payload, final Map<String, Object> properties,
            final SpanContext context) {

        Objects.requireNonNull(tenant);
        Objects.requireNonNull(device);
        Objects.requireNonNull(qos);

        log.trace("send telemetry data [tenantId: {}, deviceId: {}, qos: {}, contentType: {}, properties: {}]",
                tenant.getTenantId(), device.getDeviceId(), qos, contentType, properties);

        return send(getExchange(tenant), tenant, device, qos, contentType, payload, properties, context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return RabbitmqBasedTelemetrySender.class.getName() + " via Rabbitmq";
    }

    private String getExchange(final TenantObject tenant) {
        if (this.rabbitmqProducerConfig.isExchangePerTenant()) {
            return String.format("%s-%s", this.rabbitmqProducerConfig.getTelemetryExchange(), tenant.getTenantId());
        } else {
            return this.rabbitmqProducerConfig.getEventExchange();
        }
    }

}
