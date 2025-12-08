package com.finalyear.event.service;

import com.finalyear.event.entity.Admin;
import com.finalyear.event.payload.request.AdminUpdateRequest;
import com.finalyear.event.repository.AdminRepository;
import com.finalyear.event.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final OtpService otpService;

    public AdminService(AdminRepository adminRepository,
                        UserRepository userRepository,
                        OtpService otpService) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    public Admin register(Admin admin) {
        admin.setCreatedAt(Instant.now());
        admin.setUpdatedAt(Instant.now());
        admin.setRoles(List.of("ADMIN"));

        Admin saved = adminRepository.save(admin);
        otpService.generateAndSendOtp(saved.getEmail());
        return saved;
    }

    public boolean verifyOtp(String email, String otp) {
        return otpService.verifyOtp(email, otp);
    }

    public Admin getByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public boolean existsByEmail(String email) {
        return adminRepository.findByEmail(email).isPresent();
    }

    public boolean adminExistsById(String adminId) {
        return adminRepository.findById(adminId).isPresent();
    }

    public boolean userExistsById(String userId) {
        return userRepository.findById(userId).isPresent();
    }


    public Admin update(String adminId, AdminUpdateRequest request) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (request.getName() != null) admin.setName(request.getName());
        if (request.getEmail() != null) admin.setEmail(request.getEmail());

        admin.setUpdatedAt(Instant.now());
        return adminRepository.save(admin);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
