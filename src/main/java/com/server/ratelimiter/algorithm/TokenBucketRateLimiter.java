package com.server.ratelimiter.algorithm;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.server.ratelimiter.domain.Bucket;
import com.server.ratelimiter.enums.RateLimiterType;
import com.server.ratelimiter.service.RedisService;

import tools.jackson.databind.json.JsonMapper;

@Component
public class TokenBucketRateLimiter implements IRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(TokenBucketRateLimiter.class);

    private final RedisService redisService;
    private final JsonMapper mapper;

    private static final int DEFAULT_CAPACITY = 2;
    private static final int DEFAULT_REFILL_RATE = 1;

    public TokenBucketRateLimiter(RedisService redisService, JsonMapper mapper){
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
        Bucket bucket = getBucket(limiterKey);
        if (allowRequest(bucket)) {
            saveBucket(limiterKey, bucket);
            log.info("Request processed. key={}, remainingTokens={}", limiterKey, bucket.getCurrentToken());
            return;
        }
        log.warn("Request limit exceeded. key={}, tokens={}", limiterKey, bucket.getCurrentToken());
        throw new RuntimeException("Request limit over");
    }


    private Bucket getBucket(String limiterKey) {
        String bucketValue = redisService.get(limiterKey);

        if (bucketValue == null || bucketValue.isEmpty()) {
            Bucket bucket = new Bucket(DEFAULT_CAPACITY, DEFAULT_CAPACITY, DEFAULT_REFILL_RATE);
            log.info("Creating new bucket. key={}, capacity={}, tokens={}, refillRate={}", limiterKey, bucket.getCapacity(), bucket.getCurrentToken(), bucket.getRefillRate());
            return bucket;
        }

        try {
            Bucket bucket = mapper.readValue(bucketValue, Bucket.class);
            log.info(
                    "Bucket loaded. key={}, capacity={}, tokens={}, refillRate={}, lastRefillTime={}",
                    limiterKey,
                    bucket.getCapacity(),
                    bucket.getCurrentToken(),
                    bucket.getRefillRate(),
                    bucket.getLastRefillTime()
            );
            return bucket;
        } catch (Exception e) {
            log.error(
                    "Failed to deserialize bucket. key={}, value={}",
                    limiterKey,
                    bucketValue,
                    e
            );
            throw new RuntimeException("Invalid bucket data in Redis", e);
        }
    }

    //request verification , is it allowed or not
    private boolean allowRequest(Bucket bucket) {
        refill(bucket);
        if (bucket.getCurrentToken() <= 0) {
            return false;
        }
        bucket.setCurrentToken(bucket.getCurrentToken() - 1);
        return true;
    }

    //refill bucket
    private void refill(Bucket bucket) {

        long now = System.currentTimeMillis();
        long lastRefillTime = bucket.getLastRefillTime();
        long elapsedMillis = now - lastRefillTime;
        long elapsedSeconds = elapsedMillis / 1_000L;

        if (elapsedSeconds <= 0) {
            return;
        }
        int tokensToAdd = (int) (elapsedSeconds * bucket.getRefillRate());

        int newTokenCount = Math.min(bucket.getCapacity(), bucket.getCurrentToken() + tokensToAdd);
        bucket.setCurrentToken(newTokenCount);
        bucket.setLastRefillTime(lastRefillTime + (elapsedSeconds * 1_000L));
        log.debug(
                "Bucket refilled. added={}, tokens={}, lastRefillTime={}",
                tokensToAdd,
                bucket.getCurrentToken(),
                bucket.getLastRefillTime()
        );
    }

    //save key in redis
    private void saveBucket(String limiterKey, Bucket bucket) {
        try {
            String bucketValue = mapper.writeValueAsString(bucket);
            redisService.save(limiterKey, bucketValue);
        } catch (Exception e) {
            log.error("Failed to serialize bucket. key={}, bucket={}", limiterKey, bucket, e);
            throw new RuntimeException("Failed to save bucket", e);
        }
    }

    @Override
    public String getType() {
        return RateLimiterType.TOKEN_BUCKET.toString();
    }
}