package com.server.ratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.server.ratelimiter.enums.RateLimiterKeyType;
import com.server.ratelimiter.enums.RateLimiterType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    RateLimiterType type() default RateLimiterType.TOKEN_BUCKET;
    int capacity() default 5;
    int refillRate() default 1;
    RateLimiterKeyType key() default RateLimiterKeyType.USER;

}
