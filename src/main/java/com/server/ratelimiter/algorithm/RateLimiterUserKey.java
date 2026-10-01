package com.server.ratelimiter.algorithm;

import org.springframework.stereotype.Component;

@Component 
public class RateLimiterUserKey implements RateLimiterKey{

	@Override
	public String getKey(RateLimiterContext context) {
		if (context == null || context.getUser() == null || context.getUser().getId() == null) {
            return null;
        }
        return context.getUser().getId().toString();
	}
    
}
