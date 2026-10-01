package com.server.ratelimiter.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


import com.server.ratelimiter.algorithm.IRateLimiter;
import com.server.ratelimiter.algorithm.RateLimiterContext;
import com.server.ratelimiter.algorithm.RateLimiterKey;
import com.server.ratelimiter.annotation.RateLimit;
import com.server.ratelimiter.domain.User;
import com.server.ratelimiter.enums.RateLimiterType;
import com.server.ratelimiter.exceptions.InvalidHeaderException;
import com.server.ratelimiter.factory.RateLimiterFactory;
import com.server.ratelimiter.factory.RateLimiterKeyTypeFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class RateLimiterInterceptor implements HandlerInterceptor{
    private static final Logger log = LoggerFactory.getLogger(RateLimiterInterceptor.class);
    private final RateLimiterFactory rateLimiterFactory;
    private final RateLimiterKeyTypeFactory rateLimiterKeyTypeFactory;
    public RateLimiterInterceptor(RateLimiterFactory rateLimiterFactory, RateLimiterKeyTypeFactory rateLimiterKeyTypeFactory){
        this.rateLimiterFactory = rateLimiterFactory;
        this.rateLimiterKeyTypeFactory = rateLimiterKeyTypeFactory;
    }

    @Override 
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }
        RateLimit rateLimit = method.getMethodAnnotation(RateLimit.class);
        if(rateLimit==null){
            return true;
        }
        RateLimiterContext context = new RateLimiterContext();
        String userIdHeader = request.getHeader("X-User-Id");
        String name = request.getHeader("X-User-Name");
        String ip = request.getHeader("X-User-Ip");
        String mail = request.getHeader("X-User-Mail");
        if (userIdHeader == null || userIdHeader.isBlank()
                || name == null || name.isBlank()
                || ip == null || ip.isBlank()
                || mail == null || mail.isBlank()) {

            throw new InvalidHeaderException("Required user headers are missing");
        }
        Long userId = Long.valueOf(userIdHeader);
        User user = new User(name, userId, ip, mail);

        context.setUser(user);
        context.setApi(request.getRequestURI());

        RateLimiterType type = rateLimit.type();

        // int capacity = rateLimit.capacity();

        // int refillRate = rateLimit.refillRate();

        IRateLimiter rateLimiter =
                rateLimiterFactory.getRateLimiterInstance(type);
        RateLimiterKey key = rateLimiterKeyTypeFactory.getRateLimiterKeyInstance(rateLimit.key());
        try {
            rateLimiter.validateRequest(context, key);
            return true; // Token available -> proceed to Controller
        } catch (RuntimeException e) {
            // Rate limit exceeded -> return 429 Too Many Requests
            log.debug(e.getMessage().toString());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too Many Requests - Rate limit exceeded\"}");
            return false; // Blocks the request from reaching the controller
        }
    }

   

    
}
