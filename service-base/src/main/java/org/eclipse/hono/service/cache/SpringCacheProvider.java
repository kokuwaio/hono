/*******************************************************************************
 * Copyright (c) 2016, 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.hono.service.cache;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.hono.cache.CacheProvider;
import org.eclipse.hono.cache.ExpiringValueCache;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * A cache manager based on Spring Cache.
 *
 * @deprecated Use {@code CaffeineCacheProvider} instead.
 */
@Deprecated
public class SpringCacheProvider implements CacheProvider {

    private final CacheManager manager;
    private final Map<String, ExpiringValueCache<?, ?>> caches = new ConcurrentHashMap<>();

    /**
     * Creates a new instance based on the provided {@link CacheManager} instance.
     *
     * @param manager the cache manager to use, must not be {@code null}
     */
    public SpringCacheProvider(final CacheManager manager) {
        Objects.requireNonNull(manager);
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     *
     * @return The existing cache for the given name or a newly created one, if no
     *         cache for the given name exists yet.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <K, V> ExpiringValueCache<K, V> getCache(final String cacheName) {
        Objects.requireNonNull(cacheName);
        return (ExpiringValueCache<K, V>) caches.computeIfAbsent(cacheName, name -> {
            final Cache cache = this.manager.getCache(cacheName);
            if (cache == null) {
                return null;
            }
            return new SpringBasedExpiringValueCache<K, V>(cache);
        });
    }
}
