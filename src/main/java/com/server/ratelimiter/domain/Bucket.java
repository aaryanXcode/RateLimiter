package com.server.ratelimiter.domain;

public class Bucket {
    int capacity;
    int currentToken;
    int refillRate;  //1 token/sec
    long lastRefillTime;
    public Bucket(){

    }
    public Bucket(int capacity, int currentToken, int refillRate) {
        this.capacity = capacity;
        this.currentToken = currentToken;
        this.refillRate = refillRate;
        this.lastRefillTime = System.nanoTime();
    }
    
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(int currentToken) {
        this.currentToken = currentToken;
    }

    public int getRefillRate() {
        return refillRate;
    }

    public void setRefillRate(int refillRate) {
        this.refillRate = refillRate;
    }

    public long getLastRefillTime() {
        return lastRefillTime;
    }

    public void setLastRefillTime(long lastRefillTime) {
        this.lastRefillTime = lastRefillTime;
    }

    public boolean allowRequest() {
        refillToken();
        if (currentToken >= 1) {
            currentToken--;
            return true;
        }
        return false;
    }

    void refillToken() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastRefillTime;
        long elapsedSeconds = elapsedNanos / 1_000_000_000L;
        if (elapsedSeconds > 0) {
            int tokensToAdd = (int) (elapsedSeconds * refillRate);
            currentToken = Math.min(capacity, currentToken + tokensToAdd);
            lastRefillTime += elapsedSeconds * 1_000_000_000L;
        }
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "capacity=" + capacity +
                ", currentToken=" + currentToken +
                ", refillRate=" + refillRate +
                ", lastRefillTime=" + lastRefillTime +
                '}';
    }
}
