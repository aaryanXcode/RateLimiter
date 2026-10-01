package com.server.ratelimiter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.ratelimiter.annotation.RateLimit;
import com.server.ratelimiter.domain.User;
import com.server.ratelimiter.enums.RateLimiterKeyType;
import com.server.ratelimiter.enums.RateLimiterType;
import com.server.ratelimiter.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RequestMapping("/users")
@RestController 
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @RateLimit 
    @GetMapping("/get-user")
    public ResponseEntity<User> getUser(@RequestParam(value = "id", required = true) Long id) 
    {
        User user = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @RateLimit(type = RateLimiterType.TOKEN_BUCKET, key = RateLimiterKeyType.USER)
    @GetMapping("/all")
    public  ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.createUser(user));
    }
    
    
}
