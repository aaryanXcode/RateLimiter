package com.server.ratelimiter.domain;

public class SlidingWindowCounter {
    private int previousCount = 0;
    private int currentCount = 0;

    private long windowStartTime;
    public static final long WINDOW_SIZE = 60 * 1000L;
    public static final int MAX_REQUEST = 5;

    public SlidingWindowCounter(){

    }

    public SlidingWindowCounter(int previousCount, int currentCount) {
        this.previousCount = previousCount;
        this.currentCount = currentCount;
    }

    public SlidingWindowCounter(int previousCount, int currentCount, long windowStartTime) {
        this.previousCount = previousCount;
        this.currentCount = currentCount;
        this.windowStartTime = windowStartTime;
    }

    public int getPreviousCount() {
        return previousCount;
    }

    public void setPreviousCount(int previousCount) {
        this.previousCount = previousCount;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public long getWindowStartTime() {
        return windowStartTime;
    }

    public void setWindowStartTime(long windowStartTime) {
        this.windowStartTime = windowStartTime;
    }

    @Override
    public String toString(){
        return "SlidingWindowCounter{" +
                "windowStartTime=" + windowStartTime +
                ", previousRequestCount=" + previousCount +
                ", currentRequestCount=" + currentCount +
                '}';
    }
}
