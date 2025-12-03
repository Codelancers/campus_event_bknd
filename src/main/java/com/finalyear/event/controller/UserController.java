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
        return ResponseEntity.ok(userService.register(request));
    }

    // -------------------- SEND OTP --------------------
    @PostMapping("/otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {

        boolean exists = userService.existsByEmail(request.getEmail());
        if (!exists) {
            return ResponseEntity.status(404).body("User not found");
        }

        otpService.generateAndSendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email successfully");
    }


    // -------------------- VERIFY OTP + RETURN USER + TOKEN --------------------
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyOtpRequest req) {

        boolean ok = userService.verifyOtp(req.getEmail(), req.getOtp());

        if (!ok) {
            return ResponseEntity.status(400)
                    .body("Invalid OTP");
        }

        User user = userService.getByEmail(req.getEmail());
        String token = jwtTokenProvider.generateToken(req.getEmail(), "STUDENT");

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    // -------------------- UPDATE USER --------------------
    @PutMapping("/{userId}")
    public ResponseEntity<?> update(
            @PathVariable String userId,
            @RequestBody UserUpdateRequest request) {

        return ResponseEntity.ok(userService.update(userId, request));
    }
}
