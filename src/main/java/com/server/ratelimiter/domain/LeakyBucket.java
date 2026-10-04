package com.server.ratelimiter.domain;

import java.util.ArrayDeque;
import java.util.Queue;

public class LeakyBucket {

    public static final int MAX_CAPACITY = 10;
    Queue<Integer> requestQueue = new ArrayDeque<>(MAX_CAPACITY);
    public final int REQUEST_PROCESS_RATE = 1;
    long lastLeakTime;

    LeakyBucket(){

    }

    public LeakyBucket(long lastLeakTime, Queue<Integer> queue){
        this.lastLeakTime = lastLeakTime;
        this.requestQueue = queue;
    }

    public Queue<Integer> getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(Queue<Integer> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public long getLastLeakTime() {
        return lastLeakTime;
    }

    public void setLastLeakTime(long lastLeakTime) {
        this.lastLeakTime = lastLeakTime;
    }
}
