package com.server.ratelimiter.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.server.ratelimiter.algorithm.RateLimiterKey;
import com.server.ratelimiter.algorithm.RateLimiterUserKey;
import com.server.ratelimiter.enums.RateLimiterKeyType;

@Component 
public class RateLimiterKeyTypeFactory {
    private final Map<RateLimiterKeyType,RateLimiterKey> rateLimitersKey;
    public RateLimiterKeyTypeFactory(RateLimiterUserKey rateLimiterUserKey) {
        this.rateLimitersKey = Map.of(
            RateLimiterKeyType.USER, rateLimiterUserKey
        );
    }

    public RateLimiterKey getRateLimiterKeyInstance(RateLimiterKeyType type){
        return rateLimitersKey.get(type);
    }
}
