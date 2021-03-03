/*
 * Copyright (c) 2020, 2021 Contributors to the Eclipse Foundation
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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.hono.rabbitmq.client.RabbitmqMessage;

import io.opentracing.propagation.TextMap;

/**
 * An adapter for injecting properties into a list of {@link RabbitmqMessage} objects.
 *
 */
public final class RabbitmqHeadersInjectAdapter implements TextMap {

    private final RabbitmqMessage message;

    /**
     * Creates an adapter for a list of {@link RabbitmqMessage} objects.
     *
     * @param message The list of {@link RabbitmqMessage} objects.
     * @throws NullPointerException if headers is {@code null}.
     */
    public RabbitmqHeadersInjectAdapter(final RabbitmqMessage message) {
        this.message = Objects.requireNonNull(message);
    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(final String key, final String value) {
        message.getHeaders().put(key, value);
    }
}
