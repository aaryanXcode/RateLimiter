package com.server.ratelimiter.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.server.ratelimiter.algorithm.FixedWindowRateLimiter;
import com.server.ratelimiter.algorithm.IRateLimiter;
import com.server.ratelimiter.algorithm.LeakyBucketRateLimiter;
import com.server.ratelimiter.algorithm.SlidingWindowCounterRateLimiter;
import com.server.ratelimiter.algorithm.SlidingWindowLogRateLimiter;
import com.server.ratelimiter.algorithm.TokenBucketRateLimiter;
import com.server.ratelimiter.enums.RateLimiterType;

@Component 
public class RateLimiterFactory {
    private final Map<RateLimiterType,IRateLimiter> rateLimiters;

    public RateLimiterFactory(TokenBucketRateLimiter tokenBucketRateLimiter,
                                LeakyBucketRateLimiter leakyBucketRateLimiter,
                                SlidingWindowCounterRateLimiter slidingWindowCounterRateLimiter,
                                SlidingWindowLogRateLimiter slidingWindowLogRateLimiter,
                                FixedWindowRateLimiter fixedWindowRateLimiter) {

        this.rateLimiters = Map.of(
            RateLimiterType.TOKEN_BUCKET, tokenBucketRateLimiter,
            RateLimiterType.LEAKY_BUCKET, leakyBucketRateLimiter,
            RateLimiterType.FIXED_WINDOW, fixedWindowRateLimiter,
            RateLimiterType.SLIDING_WINDOW_COUNTER, slidingWindowCounterRateLimiter,
            RateLimiterType.SLIDING_WINDOW_LOGS, slidingWindowLogRateLimiter
        );
    }

    public IRateLimiter getRateLimiterInstance(RateLimiterType type) {
        return rateLimiters.get(type);
    }
}
