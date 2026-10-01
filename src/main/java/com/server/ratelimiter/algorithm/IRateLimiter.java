package com.server.ratelimiter.algorithm;


public interface IRateLimiter {
    void validateRequest(RateLimiterContext rateLimiterContext, RateLimiterKey key);
    String getType();

}
