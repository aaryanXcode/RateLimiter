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
    //for testing it locally we maintained this map but for large request and user number grows internally so we need to shift redis
    private final Map<String, Bucket> perUserKeyBucket = new ConcurrentHashMap<>();

    private final Bucket globalBucket = new Bucket(2, 2, 1);
    private final RedisService redisService;
    private final JsonMapper mapper;

    public TokenBucketRateLimiter(RedisService redisService, JsonMapper mapper){
        this.redisService = redisService;
        this.mapper = mapper;
    }

    @Override
    public void validateRequest(RateLimiterContext rateLimiterContext, RateLimiterKey key) {
        
        Bucket bucket;
        if(rateLimiterContext == null || key == null){
            bucket = globalBucket;
        }else{
            String limiterKey = key.getKey(rateLimiterContext);
            if (limiterKey==null ) {
                bucket = globalBucket;
            }else if(perUserKeyBucket!=null && !perUserKeyBucket.isEmpty()) {
                bucket = perUserKeyBucket.computeIfAbsent(
                        limiterKey,
                        k -> new Bucket(2, 2, 1)
                );
            }
            else{
                String bucketValue = redisService.get(limiterKey);
                log.info("Raw redis value for {}: {}", limiterKey, bucketValue);
                try {
                    bucket = mapper.readValue(bucketValue, Bucket.class);
                    log.info(
                        "Bucket deserialized successfully - capacity: {}, currentToken: {}, refillRate: {}, lastRefillTime: {}",
                        bucket.getCapacity(),
                        bucket.getCurrentToken(),
                        bucket.getRefillRate(),
                        bucket.getLastRefillTime()
                    );

                } catch (Exception e) {
                    log.error(
                        "Failed to deserialize Bucket. Redis key: {}, Redis value: {}",
                        limiterKey,
                        bucketValue,
                        e
                    );
                }
                if (bucketValue == null || bucketValue.isEmpty()) {
                    bucket = new Bucket(2, 2, 1);

                    redisService.save(limiterKey, mapper.writeValueAsString(bucket));
                }else{
                    bucket = mapper.readValue(bucketValue, Bucket.class);
                }
                
            }
        }

        if(bucket.allowRequest()){
            log.info("Request processed");
            log.info("{}", bucket);
            return;
        }
        log.warn("Request limit exceeded");
        throw new RuntimeException("Request limit over");
    }

    @Override
    public String getType() {
        return RateLimiterType.TOKEN_BUCKET.toString();
    }
}