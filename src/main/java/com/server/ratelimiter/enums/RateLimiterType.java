package com.server.ratelimiter.enums;

public enum RateLimiterType {
    LEAKY_BUCKET,
    TOKEN_BUCKET,
    SLIDING_WINDOW_LOGS,
    FIXED_WINDOW,
    SLIDING_WINDOW_COUNTER
}
