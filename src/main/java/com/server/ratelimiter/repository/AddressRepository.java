package com.server.ratelimiter.repository;

import com.server.ratelimiter.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;



public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
}
