package com.server.ratelimiter.algorithm;

public interface RateLimiterKey {
    String getKey(RateLimiterContext context);
}
