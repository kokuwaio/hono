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

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonObject;

/**
 * Dummy.
 */
public class RabbitmqMessage {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    private String body;
    private String contentType;
    private String contentEncoding;
    private Map<String, Object> headers = new HashMap<>();
    private int deliveryMode;
    private int priority;
    private String correlationId;
    private String replyTo;
    private String expiration;
    private String messageId;
    private Date timestamp = new Date(System.currentTimeMillis());
    private String type;
    private String userId;
    private String appId;
    private String clusterId;

    /**
     * Creates a new RabbitMQ message.
     */
    public RabbitmqMessage() {
    }

    /**
     * Gets the body from the message.
     *
     * @return The body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the message.
     *
     * @param body The body.
     */
    public void setBody(final String body) {
        this.body = body;
    }

    /**
     * Gets the content type from the message.
     *
     * @return The content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type of the message.
     *
     * @param contentType The content type.
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the content encoding of the message.
     *
     * @return The content encoding.
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * Sets the content encoding of the message.
     *
     * @param contentEncoding The content encoding.
     */
    public void setContentEncoding(final String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * Gets the headers from the message.
     *
     * @return The content encoding.
     */
    public Map<String, Object> getHeaders() {
        return headers;
    }

    /**
     * Sets the headers of the message.
     *
     * @param headers The headers.
     */
    public void setHeaders(final Map<String, Object> headers) {
        this.headers = headers;
    }

    /**
     * Gets the delivery mode from the message.
     *
     * @return The delivery mode.
     */
    public int getDeliveryMode() {
        return deliveryMode;
    }

    /**
     * Sets the delivery mode of the message.
     *
     * @param deliveryMode The delivery mode.
     */
    public void setDeliveryMode(final int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    /**
     * Gets the priority from the message.
     *
     * @return The priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the message.
     *
     * @param priority The priority.
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Gets the correlation id from the message.
     *
     * @return The correlation id.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the correlation id of the message.
     *
     * @param correlationId The correlation id.
     */
    public void setCorrelationId(final String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Gets the reply to from the message.
     *
     * @return The reply to.
     */
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * Sets the reply to of the message.
     *
     * @param replyTo The reply to.
     */
    public void setReplyTo(final String replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * Gets the expiration from the message.
     *
     * @return The expiration.
     */
    public String getExpiration() {
        return expiration;
    }

    /**
     * Sets the expiration of the message.
     *
     * @param expiration The expiration.
     */
    public void setExpiration(final String expiration) {
        this.expiration = expiration;
    }

    /**
     * Gets the message id from the message.
     *
     * @return The message id.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the message id of the message.
     *
     * @param messageId The message id.
     */
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the timestamp from the message.
     *
     * @return The timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the message.
     *
     * @param timestamp The timestamp.
     */
    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the type from the message.
     *
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the message.
     *
     * @param type The type.
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Gets the user id from the message.
     *
     * @return The user id.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id of the message.
     *
     * @param userId The user id.
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * Gets the app id from the message.
     *
     * @return The app id.
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Sets the app id of the message.
     *
     * @param appId The app id.
     */
    public void setAppId(final String appId) {
        this.appId = appId;
    }

    /**
     * Gets the cluster id from the message.
     *
     * @return The cluster id.
     */
    public String getClusterId() {
        return clusterId;
    }

    /**
     * Sets the cluster id of the message.
     *
     * @param clusterId The cluster id.
     */
    public void setClusterId(final String clusterId) {
        this.clusterId = clusterId;
    }

    /**
     * Creates a {@link JsonObject} for the Rabbitmq client.
     *
     * @return {@link JsonObject}.
     */
    public JsonObject toJsonObject() {
        return new JsonObject().put(RabbitmqMessageConstants.BODY_KEY, this.getBody())
                .put(RabbitmqMessageConstants.CONTENT_TYPE_KEY, this.getContentType())
                .put(RabbitmqMessageConstants.CONTENT_ENCODING_KEY, this.getContentEncoding())
                .put(RabbitmqMessageConstants.HEADERS_KEY, this.getHeaders())
                .put(RabbitmqMessageConstants.DELIVERY_MODE_KEY, this.getDeliveryMode())
                .put(RabbitmqMessageConstants.PRIORITY_KEY, this.getPriority())
                .put(RabbitmqMessageConstants.CORRELATION_ID_KEY, this.getCorrelationId())
                .put(RabbitmqMessageConstants.REPLY_TO_KEY, this.getReplyTo())
                .put(RabbitmqMessageConstants.EXPIRATION_KEY, this.getExpiration())
                .put(RabbitmqMessageConstants.MESSAGE_ID_KEY, this.getMessageId())
                .put(RabbitmqMessageConstants.TIMESTAMP_KEY,
                        dateTimeFormatter
                                .format(this.getTimestamp().toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime()))
                .put(RabbitmqMessageConstants.TYPE_KEY, this.getType())
                .put(RabbitmqMessageConstants.USER_ID_KEY, this.getUserId())
                .put(RabbitmqMessageConstants.APP_ID_KEY, this.getAppId())
                .put(RabbitmqMessageConstants.CLUSTER_ID_KEY, this.getClusterId());
    }
}
