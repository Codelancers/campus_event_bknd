package com.finalyear.event.repository;


import java.util.List;
import java.util.Optional;


import org.springframework.data.mongodb.repository.MongoRepository;



import com.finalyear.event.entity.User;


public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    
    Optional<User> findByRollNo(String rollNo);
    
    List<User> findByDepartment(String department);

    List<User> findByDepartmentIn(List<String> departments);
}