/*
 * Copyright 2024 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ctrip.framework.apollo.portal.component;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter for API requests to prevent abuse
 */
@Component
public class RateLimiter {
    
    private final LoadingCache<String, AtomicInteger> counters;
    private final PortalConfig portalConfig;
    
    public RateLimiter(PortalConfig portalConfig) {
        this.portalConfig = portalConfig;
        
        counters = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, AtomicInteger>() {
                @Override
                public AtomicInteger load(String key) {
                    return new AtomicInteger(0);
                }
            });
    }
    
    /**
     * Check if the user has exceeded the rate limit
     * @param userId the user ID
     * @throws BadRequestException if the rate limit is exceeded
     */
    public void checkRateLimit(String userId) {
        int limit = portalConfig.permissionApiRateLimit();
        AtomicInteger counter = counters.getUnchecked(userId);
        
        if (counter.incrementAndGet() > limit) {
            throw new BadRequestException("Rate limit exceeded for permission API");
        }
    }
}
