package com.server.ratelimiter.repository;

import com.server.ratelimiter.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
}
