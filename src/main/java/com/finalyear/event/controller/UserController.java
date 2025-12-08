package com.finalyear.event.controller;

import com.finalyear.event.entity.User;
import com.finalyear.event.payload.request.RegisterUserRequest;
import com.finalyear.event.payload.request.UserUpdateRequest;
import com.finalyear.event.payload.request.OtpRequest;
import com.finalyear.event.payload.request.VerifyOtpRequest;
import com.finalyear.event.payload.response.AuthResponse;
import com.finalyear.event.security.JwtTokenProvider;
import com.finalyear.event.service.OtpService;
import com.finalyear.event.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService,
                          OtpService otpService,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.otpService = otpService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // -------------------- REGISTER USER --------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {

        if (userService.userExistsByEmail(request.getEmail())) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("status", 409);
            res.put("message", "User already exists");
            return ResponseEntity.status(409).body(res);
        }

        User created = userService.register(request);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", 200);
        res.put("message", "User registered successfully");
        res.put("data", created);

        return ResponseEntity.ok(res);
    }

    // ============================
    // SEND OTP
    // ============================
    @PostMapping("/otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {

        if (!userService.userExistsByEmail(request.getEmail())) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("status", 404);
            res.put("message", "User not found");
            return ResponseEntity.status(404).body(res);
        }

        otpService.generateAndSendOtp(request.getEmail());

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", 200);
        res.put("message", "OTP sent successfully");

        return ResponseEntity.ok(res);
    }

    // ============================
    // VERIFY OTP
    // ============================
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyOtpRequest req) {

        if (!userService.userExistsByEmail(req.getEmail())) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("status", 404);
            res.put("message", "User not found");
            return ResponseEntity.status(404).body(res);
        }

        boolean ok = userService.verifyOtp(req.getEmail(), req.getOtp());
        if (!ok) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("status", 400);
            res.put("message", "Invalid OTP");
            return ResponseEntity.status(400).body(res);
        }

        User user = userService.getByEmail(req.getEmail());
        String token = jwtTokenProvider.generateToken(req.getEmail(), "STUDENT");

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", 200);
        res.put("message", "OTP verified successfully");
        res.put("token", token);
        res.put("user", user);

        return ResponseEntity.ok(res);
    }

    // ============================
    // UPDATE USER
    // ============================
    @PutMapping("/{userId}")
    public ResponseEntity<?> update(@PathVariable String userId,
                                    @RequestBody UserUpdateRequest request) {

        if (!userService.userExistsById(userId)) {
            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("status", 404);
            res.put("message", "User not found");
            return ResponseEntity.status(404).body(res);
        }

        User updated = userService.update(userId, request);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("status", 200);
        res.put("message", "User updated successfully");
        res.put("data", updated);

        return ResponseEntity.ok(res);
    }
}
