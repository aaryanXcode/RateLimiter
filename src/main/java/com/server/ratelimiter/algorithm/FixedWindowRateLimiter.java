package com.server.ratelimiter.algorithm;

import com.server.ratelimiter.domain.Bucket;
import com.server.ratelimiter.domain.FixedWindow;
import com.server.ratelimiter.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.server.ratelimiter.enums.RateLimiterType;
import tools.jackson.databind.json.JsonMapper;

@Component
public class FixedWindowRateLimiter implements IRateLimiter{
    private static final Logger log = LoggerFactory.getLogger(FixedWindowRateLimiter.class);


    private final RedisService redisService;
    private final JsonMapper jsonMapper;

    public FixedWindowRateLimiter(RedisService redisService, JsonMapper jsonMapper){
        this.redisService = redisService;
        this.jsonMapper = jsonMapper;
    }
    
    @Override
    public void validateRequest(RateLimiterContext rateLimiterContext, RateLimiterKey key) {
        if(rateLimiterContext == null || key == null){
            return;
        }
        String limiterKey = key.getKey(rateLimiterContext);
        if (limiterKey == null) {
            return;
        }

        FixedWindow window = getUserWindow(limiterKey);
        if (allowRequest(window)) {
            saveWindow(limiterKey, window);
            log.info("Request processed. key={}, requestCount={}", limiterKey, window.getRequestCount());
            return;
        }
        log.warn("Request limit exceeded. key={}, requestCount={}", limiterKey, window.getRequestCount());
        throw new RuntimeException("Request limit over");
    }

    boolean allowRequest(FixedWindow window){
        long currentTimeInMillis = System.currentTimeMillis();
        long windowStartTime = window.getStartTime();
        if(currentTimeInMillis - windowStartTime>=FixedWindow.WINDOW_SIZE){
            window.setRequestCount(1);
            window.setStartTime(currentTimeInMillis);
            return true;
        }
        if(window.getRequestCount()<FixedWindow.MAX_REQUEST_LIMIT){
            window.setRequestCount(window.getRequestCount() + 1);
            return true;
        }
        return false;
    }

    void saveWindow(String limiterKey, FixedWindow window){
        try {
            String userWindow = jsonMapper.writeValueAsString(window);
            redisService.save(limiterKey, userWindow);
        } catch (Exception e) {
            log.error("Failed to serialize window. key={}, window={}", limiterKey, window, e);
            throw new RuntimeException("Failed to save bucket", e);
        }
    }

    FixedWindow getUserWindow(String key){
        String userWindow = redisService.get(key);
        if (userWindow == null || userWindow.isEmpty()) {
            FixedWindow newUserWindow = new FixedWindow(0, System.currentTimeMillis());
            log.info("Creating new window. requestCount={}, startTime={}", newUserWindow.getRequestCount(), newUserWindow.getStartTime());
            return newUserWindow;
        }

        try {
            FixedWindow userFixedWindow = jsonMapper.readValue(userWindow, FixedWindow.class);
            log.info("userFixedWindow loaded. key={}, requestCount={}, startTime={}", key, userFixedWindow.getRequestCount(), userFixedWindow.getStartTime());
            return userFixedWindow;
        } catch (Exception e) {
            log.error("Failed to deserialize bucket. key={}, value={}", key, userWindow, e);
            throw new RuntimeException("Invalid FixedWindow data in Redis", e);
        }

    }



    @Override
    public String getType() {
         return RateLimiterType.FIXED_WINDOW.toString();
    }
}
