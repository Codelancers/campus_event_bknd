package com.finalyear.event.repository;


import java.util.Optional;


import org.springframework.data.mongodb.repository.MongoRepository;


import com.finalyear.event.entity.Admin;


public interface AdminRepository extends MongoRepository<Admin, String> {
Optional<Admin> findByEmail(String email);
}