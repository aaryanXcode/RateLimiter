package com.server.ratelimiter.exceptions;

public class InvalidHeaderException extends RuntimeException{
    public InvalidHeaderException(String messString){
        super(messString);
    }
}
