package com.server.ratelimiter.controller;

import com.server.ratelimiter.annotation.RateLimit;
import com.server.ratelimiter.domain.AddressDTO;
import com.server.ratelimiter.enums.RateLimiterKeyType;
import com.server.ratelimiter.enums.RateLimiterType;
import com.server.ratelimiter.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService){
        this.addressService = addressService;
    }

    @RateLimit(type = RateLimiterType.FIXED_WINDOW, key = RateLimiterKeyType.USER)
    @GetMapping("/get-all")
    ResponseEntity<List<AddressDTO>> getAllAddress(){
        return ResponseEntity.ok().body(addressService.getAllAddress());
    }

    @PostMapping("/create")
    ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO address){
        return ResponseEntity.ok().body(addressService.createAddress(address));
    }

    @RateLimit(type = RateLimiterType.SLIDING_WINDOW_LOGS, key = RateLimiterKeyType.USER)
    @GetMapping("/get-all/cities")
    ResponseEntity<List<String>> getAllCities(){
        return ResponseEntity.ok().body(addressService.getAllCities());
    }

    @RateLimit(type = RateLimiterType.SLIDING_WINDOW_COUNTER, key = RateLimiterKeyType.USER)
    @GetMapping("/get-all/streets")
    ResponseEntity<List<String>> getAllStreets(){
        return ResponseEntity.ok().body(addressService.getAllStreets());
    }

}
