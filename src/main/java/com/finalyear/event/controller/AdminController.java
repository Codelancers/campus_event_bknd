package com.finalyear.event.controller;

import com.finalyear.event.entity.Admin;
import com.finalyear.event.payload.request.AdminUpdateRequest;
import com.finalyear.event.payload.request.OtpRequest;
import com.finalyear.event.payload.request.VerifyOtpRequest;
import com.finalyear.event.payload.response.AuthResponse;
import com.finalyear.event.service.AdminService;
import com.finalyear.event.service.OtpService;
import com.finalyear.event.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final OtpService otpService;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminController(AdminService adminService, OtpService otpService, JwtTokenProvider jwtTokenProvider) {
        this.adminService = adminService;
        this.otpService = otpService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Register admin
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Admin admin) {
        return ResponseEntity.ok(adminService.register(admin));
    }

    // Send OTP for login
    @PostMapping("/otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {

        boolean exists = adminService.existsByEmail(request.getEmail());
        if (!exists) {
            return ResponseEntity.status(404).body("Admin not found");
        }

        otpService.generateAndSendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }


    // Verify OTP
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyOtpRequest req) {
        boolean ok = adminService.verifyOtp(req.getEmail(), req.getOtp());

        if (!ok) {
            return ResponseEntity.status(400).body("Invalid OTP");
        }

        Admin admin = adminService.getByEmail(req.getEmail());  // fetch admin

        String token = jwtTokenProvider.generateToken(req.getEmail(), "ADMIN");

        return ResponseEntity.ok(new AuthResponse(token, admin));
    }



    // Update admin info
    @PutMapping("/{adminId}")
    public ResponseEntity<?> update(@PathVariable String adminId,
                                    @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(adminService.update(adminId, request));
    }

    // Delete a user (Admin-only)
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }
}
