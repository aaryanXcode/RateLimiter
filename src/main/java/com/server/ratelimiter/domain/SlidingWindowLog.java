package com.server.ratelimiter.domain;

import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowLog {

    public static final int MAX_REQUEST = 5;
    public static final long WINDOW_SIZE = 60 * 1000L;
    Deque<Long> timeStamps = new ArrayDeque<>(MAX_REQUEST);

    long windowStartTime;

    public SlidingWindowLog(){

    }
    public SlidingWindowLog(long windowStartTime, Deque<Long> timeStamps){
        this.windowStartTime = windowStartTime;
        this.timeStamps = timeStamps;
    }

    public long getWindowStartTime() {
        return windowStartTime;
    }

    public void setWindowStartTime(long windowStartTime) {
        this.windowStartTime = windowStartTime;
    }

    public Deque<Long> getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(Deque<Long> timeStamps) {
        this.timeStamps = timeStamps;
    }
}
