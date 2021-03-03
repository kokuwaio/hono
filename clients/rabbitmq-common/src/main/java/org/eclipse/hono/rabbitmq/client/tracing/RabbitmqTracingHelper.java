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

package org.eclipse.hono.rabbitmq.client.tracing;

import java.util.Objects;

import org.eclipse.hono.rabbitmq.client.RabbitmqMessage;
import org.eclipse.hono.tracing.TracingHelper;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpanContext;
import io.opentracing.propagation.Format;
import io.opentracing.tag.IntTag;
import io.opentracing.tag.Tags;

/**
 * A helper class providing Kafka-specific utility methods for interacting with the OpenTracing API.
 *
 */
public final class RabbitmqTracingHelper {

    /**
     * An OpenTracing tag that contains the offset of a Kafka record.
     */
    public static final LongTag TAG_OFFSET = new LongTag("offset");

    /**
     * An OpenTracing tag that contains the partition of a Kafka record.
     */
    public static final IntTag TAG_PARTITION = new IntTag("partition");

    /**
     * An OpenTracing tag that contains the timestamp of a Kafka record.
     */
    public static final LongTag TAG_TIMESTAMP = new LongTag("timestamp");

    private RabbitmqTracingHelper() {
        // prevent instantiation
    }

    /**
     * Creates a new <em>OpenTracing</em> span to trace producing messages to Kafka.
     * <p>
     * The returned span will already contain the following tags:
     * <ul>
     * <li>{@link Tags#COMPONENT} - set to <em>hono-client-rabbitmq</em></li>
     * <li>{@link Tags#MESSAGE_BUS_DESTINATION} - set to {@code To_<topic>}</li>
     * <li>{@link Tags#SPAN_KIND} - set to {@link Tags#SPAN_KIND_PRODUCER}</li>
     * <li>{@link Tags#PEER_SERVICE} - set to <em>Rabbitmq</em></li>
     * </ul>
     *
     * @param tracer The Tracer to use.
     * @param exchange The topic from which the operation name is derived.
     * @param referenceType The type of reference towards the span context.
     * @param parent The span context to set as parent and to derive the sampling priority from (may be null).
     * @return The new span.
     * @throws NullPointerException if tracer or topic is {@code null}.
     */
    public static Span newProducerSpan(final Tracer tracer, final String exchange, final String referenceType,
            final SpanContext parent) {
        Objects.requireNonNull(tracer);
        Objects.requireNonNull(exchange);
        Objects.requireNonNull(referenceType);

        return TracingHelper.buildSpan(tracer, parent, "To_" + exchange, referenceType)
                .ignoreActiveSpan()
                .withTag(Tags.COMPONENT.getKey(), "hono-client-rabbitmq")
                .withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_PRODUCER)
                .withTag(Tags.MESSAGE_BUS_DESTINATION.getKey(), exchange)
                .withTag(Tags.PEER_SERVICE.getKey(), "rabbitmq")
                .start();
    }

    /**
     * Injects a {@code SpanContext} into a Kafka record.
     * <p>
     * The span context will be written to the record headers.
     *
     * @param tracer The Tracer to use for injecting the context.
     * @param message The Rabbitmq message.
     * @param spanContext The context to inject or {@code null} if no context is available.
     * @throws NullPointerException if tracer or record is {@code null}.
     */
    public static void injectSpanContext(final Tracer tracer, final RabbitmqMessage message,
            final SpanContext spanContext) {

        Objects.requireNonNull(tracer);
        Objects.requireNonNull(message);

        if (spanContext != null && !(spanContext instanceof NoopSpanContext)) {
            tracer.inject(spanContext, Format.Builtin.TEXT_MAP, new RabbitmqHeadersInjectAdapter(message));
        }
    }

}
