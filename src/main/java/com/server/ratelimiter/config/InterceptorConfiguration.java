package com.server.ratelimiter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.server.ratelimiter.interceptor.RateLimiterInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
    private final RateLimiterInterceptor rateLimiterInterceptor;

    public InterceptorConfiguration(RateLimiterInterceptor rateLimiterInterceptor){
        this.rateLimiterInterceptor = rateLimiterInterceptor;
    }

    @Override 
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(rateLimiterInterceptor);
    }

}
