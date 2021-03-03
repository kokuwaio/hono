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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.eclipse.hono.config.ProtocolAdapterProperties;
import org.eclipse.hono.rabbitmq.client.CachingRabbitmqProducerFactory;
import org.eclipse.hono.rabbitmq.client.RabbitmqProducerConfigProperties;
import org.eclipse.hono.util.RegistrationAssertion;
import org.eclipse.hono.util.TenantObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.junit5.VertxExtension;
import io.vertx.rabbitmq.RabbitMQOptions;

/**
 * Verifies behavior of {@link RabbitmqBasedEventSender}.
 */
@ExtendWith(VertxExtension.class)
public class RabbitmqBasedEventSenderTest {

    private final TenantObject tenant = new TenantObject("the-tenant", true);
    private final RegistrationAssertion device = new RegistrationAssertion("the-device");
    private final ProtocolAdapterProperties adapterConfig = new ProtocolAdapterProperties();
    private RabbitmqProducerConfigProperties rabbitmqProducerConfig;
    private final Tracer tracer = NoopTracerFactory.create();

    /**
     * Sets up the fixture.
     */
    @BeforeEach
    public void setUp() {
        rabbitmqProducerConfig = new RabbitmqProducerConfigProperties();
        rabbitmqProducerConfig.setProducerConfig(new RabbitMQOptions());
    }

    /**
     * Verifies that the constructor throws a nullpointer exception if a parameter is {@code null}.
     */
    @Test
    public void testThatConstructorThrowsOnMissingParameter() {
        final CachingRabbitmqProducerFactory factory = mock(CachingRabbitmqProducerFactory.class);

        assertThrows(NullPointerException.class,
                () -> new RabbitmqBasedEventSender(null, rabbitmqProducerConfig, adapterConfig, tracer));

        assertThrows(NullPointerException.class,
                () -> new RabbitmqBasedEventSender(factory, null, adapterConfig, tracer));

        assertThrows(NullPointerException.class,
                () -> new RabbitmqBasedEventSender(factory, rabbitmqProducerConfig, adapterConfig, null));
    }

    /**
     * Verifies that
     * {@link RabbitmqBasedEventSender#sendEvent(TenantObject, RegistrationAssertion, String, Buffer, Map, SpanContext)}
     * throws a nullpointer exception if a mandatory parameter is {@code null}.
     */
    @Test
    public void testThatSendEventThrowsOnMissingMandatoryParameter() {
        final CachingRabbitmqProducerFactory factory = mock(CachingRabbitmqProducerFactory.class);
        final RabbitmqBasedEventSender sender = new RabbitmqBasedEventSender(factory, rabbitmqProducerConfig,
                adapterConfig,
                tracer);

        assertThrows(NullPointerException.class,
                () -> sender.sendEvent(null, device, "the-content-type", null, null, null));

        assertThrows(NullPointerException.class,
                () -> sender.sendEvent(tenant, null, "the-content-type", null, null, null));

    }

}
