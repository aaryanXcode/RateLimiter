package com.server.ratelimiter.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service 
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(RedisTemplate<String, String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, String value){
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key){
        return redisTemplate.delete(key);
    }

    public Boolean isExist(String key){
        return redisTemplate.hasKey(key);
    }
}
