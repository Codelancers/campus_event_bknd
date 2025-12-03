package com.finalyear.event.repository;


import java.util.Optional;


import org.springframework.data.mongodb.repository.MongoRepository;


import com.finalyear.event.entity.User;


public interface UserRepository extends MongoRepository<User, String> {
Optional<User> findByEmail(String email);
}