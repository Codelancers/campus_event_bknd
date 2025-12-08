package com.finalyear.event.controller;

import com.finalyear.event.entity.Admin;
import com.finalyear.event.payload.request.AdminUpdateRequest;
import com.finalyear.event.payload.request.OtpRequest;
import com.finalyear.event.payload.request.VerifyOtpRequest;
import com.finalyear.event.payload.response.AuthResponse;
import com.finalyear.event.service.AdminService;
import com.finalyear.event.service.OtpService;
import com.finalyear.event.security.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

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
            Map<String, Object> res = new HashMap<>();
            res.put("status", 404);
            res.put("success", false);
            res.put("message", "Admin not found");
            return ResponseEntity.status(404).body(res);
        }

        otpService.generateAndSendOtp(request.getEmail());

        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("success", true);
        res.put("message", "OTP sent to email");

        return ResponseEntity.ok(res);
    }

    // ============================
    // VERIFY OTP
    // ============================
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyOtpRequest req) {

        boolean ok = adminService.verifyOtp(req.getEmail(), req.getOtp());

        if (!ok) {
            Map<String, Object> res = new HashMap<>();
            res.put("status", 400);
            res.put("success", false);
            res.put("message", "Invalid OTP");
            return ResponseEntity.status(400).body(res);
        }

        Admin admin = adminService.getByEmail(req.getEmail());
        String token = jwtTokenProvider.generateToken(req.getEmail(), "ADMIN");

        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("success", true);
        res.put("token", token);
        res.put("admin", admin);

        return ResponseEntity.ok(res);
    }

    // ============================
    // UPDATE ADMIN
    // ============================
    @PutMapping("/{adminId}")
    public ResponseEntity<?> update(@PathVariable String adminId,
                                    @RequestBody AdminUpdateRequest request) {

        // Check if admin exists
        if (!adminService.adminExistsById(adminId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("status", 404);
            res.put("success", false);
            res.put("message", "Admin not found");
            return ResponseEntity.status(404).body(res);
        }

        Admin updated = adminService.update(adminId, request);

        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("success", true);
        res.put("message", "Admin updated successfully");
        res.put("admin", updated);

        return ResponseEntity.ok(res);
    }



    // ============================
    // DELETE USER (ADMIN ONLY)
    // ============================
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {

        // Check if user exists
        if (!adminService.userExistsById(userId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("status", 404);
            res.put("success", false);
            res.put("message", "User not found");
            return ResponseEntity.status(404).body(res);
        }

        adminService.deleteUser(userId);

        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("success", true);
        res.put("message", "User deleted successfully");

        return ResponseEntity.ok(res);
    }

}
