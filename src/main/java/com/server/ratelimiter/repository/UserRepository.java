package com.server.ratelimiter.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.server.ratelimiter.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
    Optional<UserEntity> findByGmail(String gmail);
    Optional<UserEntity> findByIpAddress(String ipAddress);
    boolean existsByGmail(String gmail);
    boolean existsByIpAddress(String ipAddress);
}
