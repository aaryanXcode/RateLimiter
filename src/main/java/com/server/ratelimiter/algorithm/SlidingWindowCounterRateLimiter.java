package com.server.ratelimiter.algorithm;

import com.server.ratelimiter.domain.SlidingWindowCounter;
import com.server.ratelimiter.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.server.ratelimiter.enums.RateLimiterType;
import tools.jackson.databind.json.JsonMapper;

import static com.server.ratelimiter.domain.SlidingWindowCounter.MAX_REQUEST;
import static com.server.ratelimiter.domain.SlidingWindowCounter.WINDOW_SIZE;

@Component
public class SlidingWindowCounterRateLimiter implements IRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(SlidingWindowCounterRateLimiter.class);

    private final RedisService redisService;
    private final JsonMapper jsonMapper;

    public SlidingWindowCounterRateLimiter(RedisService redisService, JsonMapper jsonMapper){
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

        SlidingWindowCounter window = getUserWindow(limiterKey);
        if (allowRequest(window)) {
            saveWindow(limiterKey, window);
            log.info("Request processed. key={}, window={}", limiterKey, window.toString());
            return;
        }
        log.warn("Request limit exceeded. key={}, window={}", limiterKey, window.toString());
        throw new RuntimeException("Request limit over");
    }



    boolean allowRequest(SlidingWindowCounter window) {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - window.getWindowStartTime();

        if (elapsed >= WINDOW_SIZE) {
            window.setPreviousCount(window.getCurrentCount());
            window.setCurrentCount(0);
            window.setWindowStartTime(window.getWindowStartTime() + WINDOW_SIZE);
            elapsed = currentTime - window.getWindowStartTime();
        }

        double weight = (double) (WINDOW_SIZE - elapsed) / WINDOW_SIZE;
        double estimatedCount = window.getPreviousCount() * weight + window.getCurrentCount();
        if (estimatedCount < MAX_REQUEST) {
            window.setCurrentCount(window.getCurrentCount() + 1);
            return true;
        }
        return false;
    }



    void saveWindow(String limiterKey, SlidingWindowCounter window){
        try {
            String userWindow = jsonMapper.writeValueAsString(window);
            redisService.save(limiterKey, userWindow);
        } catch (Exception e) {
            log.error("Failed to serialize window. key={}, window={}", limiterKey, window, e);
            throw new RuntimeException("Failed to save sliding window counter", e);
        }
    }

    SlidingWindowCounter getUserWindow(String key){
        String userWindow = redisService.get(key);
        if (userWindow == null || userWindow.isEmpty()) {
            SlidingWindowCounter newUserWindow = new SlidingWindowCounter(0, 0, System.currentTimeMillis());
            log.info("Creating new window. startTime={}, prevCount={}, currentCount={}", newUserWindow.getWindowStartTime(), newUserWindow.getPreviousCount(), newUserWindow.getCurrentCount());
            return newUserWindow;
        }

        try {
            SlidingWindowCounter slidingWindowCounter = jsonMapper.readValue(userWindow, SlidingWindowCounter.class);
            log.info("userSlidingWindowCounter loaded. key={}, startTime={}, prevCount={}, currentCount={}", key, slidingWindowCounter.getWindowStartTime(), slidingWindowCounter.getPreviousCount(), slidingWindowCounter.getCurrentCount());
            return slidingWindowCounter;
        } catch (Exception e) {
            log.error("Failed to deserialize slidingWindowCounter. key={}, value={}", key, userWindow, e);
            throw new RuntimeException("Invalid SlidingWindowCounter data in Redis", e);
        }

    }

    @Override
    public String getType() {
         return RateLimiterType.SLIDING_WINDOW_COUNTER.toString();
    }

	
}
