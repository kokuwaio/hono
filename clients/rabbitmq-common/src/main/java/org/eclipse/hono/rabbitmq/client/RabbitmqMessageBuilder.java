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

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Dummy.
 */
public class RabbitmqMessageBuilder {

    private RabbitmqMessage message = new RabbitmqMessage();

    /**
     * Creates a new RabbitMQ message builder.
     */
    public RabbitmqMessageBuilder() {
    }

    /**
     * Creates a new RabbitMQ message builder.
     *
     * @param message initial rabbitmq message
     */
    public RabbitmqMessageBuilder(final RabbitmqMessage message) {
        Objects.requireNonNull(message);
        this.message = message;
    }

    /**
     * Sets the body of the message.
     *
     * @param body The body.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setBody(final String body) {
        this.message.setBody(body);
        return this;
    }

    /**
     * Sets the content type of the message.
     *
     * @param contentType The content type.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setContentType(final String contentType) {
        this.message.setContentType(contentType);
        return this;
    }

    /**
     * Sets the content encoding of the message.
     *
     * @param contentEncoding The content encoding.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setContentEncoding(final String contentEncoding) {
        this.message.setContentEncoding(contentEncoding);
        return this;
    }

    /**
     * Sets the headers of the message.
     *
     * @param headers The headers.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setHeaders(final Map<String, Object> headers) {
        this.message.setHeaders(headers);
        return this;
    }

    /**
     * Add or update a specific header.
     *
     * @param key The header key.
     * @param value The header value.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder addHeader(final String key, final Object value) {
        this.message.getHeaders().put(key, value);
        return this;
    }

    /**
     * Sets the delivery mode of the message.
     *
     * @param deliveryMode The delivery mode.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setDeliveryMode(final int deliveryMode) {
        this.message.setDeliveryMode(deliveryMode);
        return this;
    }

    /**
     * Sets the priority of the message.
     *
     * @param priority The priority.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setPriority(final int priority) {
        this.message.setPriority(priority);
        return this;
    }

    /**
     * Sets the correlation id of the message.
     *
     * @param correlationId The correlation id.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setCorrelationId(final String correlationId) {
        this.message.setCorrelationId(correlationId);
        return this;
    }

    /**
     * Sets the reply to of the message.
     *
     * @param replyTo The reply to.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setReplyTo(final String replyTo) {
        this.message.setReplyTo(replyTo);
        return this;
    }

    /**
     * Sets the expiration of the message.
     *
     * @param expiration The expiration.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setExpiration(final String expiration) {
        this.message.setExpiration(expiration);
        return this;
    }

    /**
     * Sets the message id of the message.
     *
     * @param messageId The message id.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setMessageId(final String messageId) {
        this.message.setMessageId(messageId);
        return this;
    }

    /**
     * Sets the timestamp of the message.
     *
     * @param timestamp The timestamp.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setTimestamp(final Date timestamp) {
        this.message.setTimestamp(timestamp);
        return this;
    }

    /**
     * Sets the type of the message.
     *
     * @param type The type.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setType(final String type) {
        this.message.setType(type);
        return this;
    }

    /**
     * Sets the app id of the message.
     *
     * @param userId The app id.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setUserId(final String userId) {
        this.message.setUserId(userId);
        return this;
    }

    /**
     * Sets the app id of the message.
     *
     * @param appId The app id.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setAppId(final String appId) {
        this.message.setAppId(appId);
        return this;
    }

    /**
     * Sets the cluster id of the message.
     *
     * @param clusterId The cluster id.
     * @return The message builder.
     */
    public RabbitmqMessageBuilder setClusterId(final String clusterId) {
        this.message.setClusterId(clusterId);
        return this;
    }

    /**
     * Builds the message.
     *
     * @return The message.
     */
    public RabbitmqMessage build() {
        return message;
    }

}
