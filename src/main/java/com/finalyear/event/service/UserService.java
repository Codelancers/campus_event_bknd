package com.finalyear.event.service;

import com.finalyear.event.entity.User;
import com.finalyear.event.payload.request.RegisterUserRequest;
import com.finalyear.event.payload.request.UserUpdateRequest;
import com.finalyear.event.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public UserService(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    public User register(RegisterUserRequest request) {
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setDepartment(request.getDepartment());
        user.setYear(request.getYear());
        user.setRollNo(request.getRollNo());
        user.setRoles(List.of("STUDENT"));
        user.setPoints(0);
        
        user.setRegisteredEvents(new ArrayList<>());
        user.setWonEvents(new ArrayList<>());
        user.setOtpHash("");
        user.setOtpExpiry(null);


        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        User saved = userRepository.save(user);
        otpService.generateAndSendOtp(saved.getEmail());

        return saved;
    }

    public boolean verifyOtp(String email, String otp) {
        return otpService.verifyOtp(email, otp);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean userExistsById(String userId) {
        return userRepository.findById(userId).isPresent();
    }




    public User update(String userId, UserUpdateRequest request) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (request.getName() != null) user.setName(request.getName());
    if (request.getEmail() != null) user.setEmail(request.getEmail());
    if (request.getPhone() != null) user.setPhone(request.getPhone());
    if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
    if (request.getYear() != null) user.setYear(request.getYear());
    if (request.getRollNo() != null) user.setRollNo(request.getRollNo());

    user.setUpdatedAt(Instant.now());

    return userRepository.save(user);
}

}
