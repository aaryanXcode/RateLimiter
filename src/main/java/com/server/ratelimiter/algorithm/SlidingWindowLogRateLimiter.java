package com.server.ratelimiter.algorithm;

import com.server.ratelimiter.domain.FixedWindow;
import com.server.ratelimiter.domain.SlidingWindowLog;
import com.server.ratelimiter.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.server.ratelimiter.enums.RateLimiterType;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class SlidingWindowLogRateLimiter implements IRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(SlidingWindowLogRateLimiter.class);

    private final RedisService redisService;
    private final JsonMapper jsonMapper;

    public SlidingWindowLogRateLimiter(RedisService redisService, JsonMapper jsonMapper){
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

        SlidingWindowLog window = getUserWindow(limiterKey);
        if (allowRequest(window)) {
            saveWindow(limiterKey, window);
            log.info("Request processed. key={}, timeStamps={}", limiterKey, window.getTimeStamps());
            return;
        }
        log.warn("Request limit exceeded. key={}, timeStamps={}", limiterKey, window.getTimeStamps());
        throw new RuntimeException("Request limit over");
    }

    boolean allowRequest(SlidingWindowLog window){
        long currentTimeInMillis= System.currentTimeMillis();
        //remove expired timestamps, - expired time stamps are those which is lesser then time difference of current - windowTime
        while(!window.getTimeStamps().isEmpty() && (window.getTimeStamps().peekFirst() < (currentTimeInMillis - SlidingWindowLog.WINDOW_SIZE))){
            window.getTimeStamps().removeFirst();
        }

        if(window.getTimeStamps().size()<SlidingWindowLog.MAX_REQUEST){
            window.getTimeStamps().addLast(currentTimeInMillis);
            return true;
        }
        return false;
    }

    void saveWindow(String limiterKey, SlidingWindowLog window){
        try {
            String userWindow = jsonMapper.writeValueAsString(window);
            redisService.save(limiterKey, userWindow);
        } catch (Exception e) {
            log.error("Failed to serialize window. key={}, window={}", limiterKey, window, e);
            throw new RuntimeException("Failed to save bucket", e);
        }
    }

    SlidingWindowLog getUserWindow(String key){
        String userWindow = redisService.get(key);
        if (userWindow == null || userWindow.isEmpty()) {
            SlidingWindowLog newUserWindow = new SlidingWindowLog(System.currentTimeMillis(), new ArrayDeque<>());
            log.info("Creating new window. startTime={}, timeStamps={}", newUserWindow.getWindowStartTime(), newUserWindow.getTimeStamps());
            return newUserWindow;
        }

        try {
            SlidingWindowLog userSlidingLogWindow = jsonMapper.readValue(userWindow, SlidingWindowLog.class);
            log.info("userSlidingLogWindow loaded. key={}, startTime={}, timeStamps={}", key, userSlidingLogWindow.getWindowStartTime(), userSlidingLogWindow.getTimeStamps());
            return userSlidingLogWindow;
        } catch (Exception e) {
            log.error("Failed to deserialize bucket. key={}, value={}", key, userWindow, e);
            throw new RuntimeException("Invalid SlidingWindowLog data in Redis", e);
        }

    }

   @Override
   public String getType() {
        return RateLimiterType.SLIDING_WINDOW_LOGS.toString();
   }

   
}



