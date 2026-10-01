package com.server.ratelimiter.algorithm;

import com.server.ratelimiter.domain.User;

public class RateLimiterContext {
    private User user;
    private String api;
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getApi() {
        return api;
    }
    public void setApi(String api) {
        this.api = api;
    }

    

}
