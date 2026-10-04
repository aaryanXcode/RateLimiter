package com.server.ratelimiter.algorithm;

import com.server.ratelimiter.domain.LeakyBucket;
import com.server.ratelimiter.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.server.ratelimiter.enums.RateLimiterType;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayDeque;

@Component
public class LeakyBucketRateLimiter implements IRateLimiter{
    private static final Logger log = LoggerFactory.getLogger(LeakyBucketRateLimiter.class);
    private final RedisService redisService;
    private final JsonMapper mapper;

    public LeakyBucketRateLimiter(RedisService redisService, JsonMapper mapper){
        this.redisService = redisService;
        this.mapper = mapper;
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
        LeakyBucket bucket = getBucket(limiterKey);
        if (allowRequest(bucket)) {
            saveBucket(limiterKey, bucket);
            log.info("Request processed. key={}, lastLeakTime={}, requestQueue={}", limiterKey, bucket.getLastLeakTime(), bucket.getRequestQueue().toString());
            return;
        }
        log.warn("Request limit exceeded. key={}, lastLeakTime={}, requestQueue={}", limiterKey, bucket.getLastLeakTime(), bucket.getRequestQueue().toString());
        throw new RuntimeException("Request limit over");
    }


    private LeakyBucket getBucket(String limiterKey) {
        String bucketValue = redisService.get(limiterKey);

        if (bucketValue == null || bucketValue.isEmpty()) {
            LeakyBucket bucket = new LeakyBucket(System.currentTimeMillis(), new ArrayDeque<>(LeakyBucket.MAX_CAPACITY));
            log.info("Creating new bucket. key={}, lastLeakTime={}, requestQueue={}", limiterKey, bucket.getLastLeakTime(), bucket.getRequestQueue().toString());
            return bucket;
        }

        try {
            LeakyBucket bucket = mapper.readValue(bucketValue, LeakyBucket.class);
            log.info("Bucket loaded. key={}, lastLeakTime={}, requestQueue={}", limiterKey, bucket.getLastLeakTime(), bucket.getRequestQueue());
            return bucket;
        } catch (Exception e) {
            log.error("Failed to deserialize bucket. key={}, value={}", limiterKey, bucketValue, e);
            throw new RuntimeException("Invalid bucket data in Redis", e);
        }
    }

    //request verification , is it allowed or not
    private boolean allowRequest(LeakyBucket bucket) {
        long currentTimeInMillis = System.currentTimeMillis();
        long elapsedTime = (currentTimeInMillis - bucket.getLastLeakTime())/1000;

        long numberOfRequestToClearUp = elapsedTime * bucket.REQUEST_PROCESS_RATE;

        log.info("elapsedTime and requestLeak. elapsedTime={}, requestLeak={}", elapsedTime, numberOfRequestToClearUp);
        while(!bucket.getRequestQueue().isEmpty() && numberOfRequestToClearUp>0){
            bucket.getRequestQueue().poll();
            numberOfRequestToClearUp--;
        }

        bucket.setLastLeakTime(currentTimeInMillis);

        if(bucket.getRequestQueue().size()< LeakyBucket.MAX_CAPACITY){
            bucket.getRequestQueue().offer(1);
            return true;
        }
        return false;
    }

    private void saveBucket(String limiterKey, LeakyBucket bucket) {
        try {
            String bucketValue = mapper.writeValueAsString(bucket);
            redisService.save(limiterKey, bucketValue);
        } catch (Exception e) {
            log.error("Failed to serialize leakyBucket. key={}, bucket={}", limiterKey, bucket, e);
            throw new RuntimeException("Failed to save bucket", e);
        }
    }

    @Override
    public String getType() {
         return RateLimiterType.LEAKY_BUCKET.toString();
    }
}
