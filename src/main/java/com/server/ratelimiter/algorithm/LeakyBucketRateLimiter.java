package com.server.ratelimiter.algorithm;

import org.springframework.stereotype.Component;

import com.server.ratelimiter.enums.RateLimiterType;

@Component
public class LeakyBucketRateLimiter implements IRateLimiter{
    
    @Override
    public void validateRequest(RateLimiterContext rateLimiterContext, RateLimiterKey key) {
        throw new UnsupportedOperationException("Unimplemented method 'validateRequest'");
    }

    @Override
    public String getType() {
         return RateLimiterType.LEAKY_BUCKET.toString();
    }
}
