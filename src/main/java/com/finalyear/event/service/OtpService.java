package com.finalyear.event.service;

import com.finalyear.event.entity.Admin;
import com.finalyear.event.entity.User;
import com.finalyear.event.repository.AdminRepository;
import com.finalyear.event.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
public class OtpService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OtpService(AdminRepository adminRepository,
                      UserRepository userRepository,
                      EmailService emailService) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    private String generate6DigitOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public String generateAndSendOtp(String email) {
        String otp = generate6DigitOtp();
        String hashedOtp = BCrypt.hashpw(otp, BCrypt.gensalt());
        Instant expiry = Instant.now().plus(5, ChronoUnit.MINUTES);

        // If email belongs to Admin
        adminRepository.findByEmail(email).ifPresent(admin -> {
            admin.setOtpHash(hashedOtp);
            admin.setOtpExpiry(expiry);
            adminRepository.save(admin);
        });

        // If email belongs to User
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setOtpHash(hashedOtp);
            user.setOtpExpiry(expiry);
            userRepository.save(user);
        });

        emailService.sendOtp(email, otp);

        return otp; // REMOVE IN PRODUCTION - ONLY FOR TESTING
    }

    public boolean verifyOtp(String email, String otp) {
        // Check Admin first
        var adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            if (admin.getOtpExpiry() == null || Instant.now().isAfter(admin.getOtpExpiry()))
                return false;

            if (BCrypt.checkpw(otp, admin.getOtpHash())) {
                admin.setOtpHash(null);
                admin.setOtpExpiry(null);
                adminRepository.save(admin);
                return true;
            }

            return false;
        }

        // Check User
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getOtpExpiry() == null || Instant.now().isAfter(user.getOtpExpiry()))
                return false;

            if (BCrypt.checkpw(otp, user.getOtpHash())) {
                user.setOtpHash(null);
                user.setOtpExpiry(null);
                userRepository.save(user);
                return true;
            }
        }

        return false;
    }
}
