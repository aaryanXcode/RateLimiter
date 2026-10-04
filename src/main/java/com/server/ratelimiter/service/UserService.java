package com.server.ratelimiter.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.server.ratelimiter.domain.User;
import com.server.ratelimiter.entity.UserEntity;
import com.server.ratelimiter.exceptions.DuplicateUserException;
import com.server.ratelimiter.repository.UserRepository;

@Service 
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll().stream().map(
            d-> new User(d.getName(), d.getId(), d.getIpAddress(), d.getGmail())
        ).toList();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .map(d -> new User(
                        d.getName(),
                        d.getId(),
                        d.getIpAddress(),
                        d.getGmail()))
                .orElseThrow(() -> new NoSuchElementException(
                        "User not found with id: " + id));
    }

    public String createUser(User user) {
        if (userRepository.existsByGmail(user.getGmail())  || userRepository.existsByIpAddress(user.getIpAddress())) {
            throw new DuplicateUserException("User already exists");
        }
        UserEntity entity = new UserEntity(
                user.getName(),
                user.getIpAddress(),
                user.getGmail()
        );

         try {
            UserEntity savedEntity = userRepository.save(entity);
            return String.valueOf(savedEntity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateUserException("User already exists");
        }
    }


    public List<String> getAllNames() {
        return userRepository.findAll().stream().map(UserEntity::getName).toList();
    }
}
