package com.finalyear.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String toEmail, String otp) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("projectfinalyear8685@gmail.com");   // IMPORTANT
        message.setTo(toEmail);
        message.setSubject("Your OTP for Login / Registration");

        message.setText(
            "Hello,\n\n" +
            "Your OTP is: " + otp + "\n\n" +
            "This OTP is valid for 5 minutes.\n\n" +
            "Regards,\n" +
            "College Event Management Team"
        );

        mailSender.send(message);
        logger.info("OTP email sent to {}", toEmail);

    } catch (Exception ex) {
        logger.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
    }
}

}
