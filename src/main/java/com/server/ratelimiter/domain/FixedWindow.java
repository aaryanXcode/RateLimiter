package com.server.ratelimiter.domain;

public class FixedWindow {

    long startTime;
    int requestCount;
    public static final long WINDOW_SIZE = 60 * 1000L;
    public static final int MAX_REQUEST_LIMIT = 5;

    FixedWindow(){

    }

    public FixedWindow(int requestCount, long startTime){
        this.startTime = startTime;
        this.requestCount = requestCount;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount){
        this.requestCount = requestCount;
    }

    public long getStartTime(){
        return startTime;
    }

    public void setStartTime(long startTime){
        this.startTime = startTime;
    }

    @Override
    public String toString(){
        return "FixedWindow{" +
                "startTime=" + startTime +
                ", requestCount=" + requestCount +
                '}';
    }
}
