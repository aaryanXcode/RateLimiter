package com.server.ratelimiter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.server.ratelimiter.exceptions.DuplicateUserException;
import com.server.ratelimiter.exceptions.InvalidHeaderException;

@ControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidHeaderException.class)
    public ResponseEntity<String> handleInvalidUserContext(InvalidHeaderException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<String> handleDuplicateUserException(){
         return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body("User already exists");
    }

}
