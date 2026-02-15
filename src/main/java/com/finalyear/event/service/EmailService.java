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

    public void sendEventCreatedEmail(String toEmail, String eventTitle, String department) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("projectfinalyear8685@gmail.com");
            message.setTo(toEmail);
            message.setSubject("📢 New Event Announced!");

            message.setText(
                "Hello,\n\n" +
                "A new event has been announced.\n\n" +
                "📌 Event: " + eventTitle + "\n" +
                "🏫 Department: " + (department == null ? "All Departments" : department) + "\n\n" +
                "Please login to the portal for full details.\n\n" +
                "Regards,\n" +
                "College Event Management Team"
            );

            mailSender.send(message);
            logger.info("Event email sent to {}", toEmail);

        } catch (Exception ex) {
            logger.error("Failed to send event email to {}: {}", toEmail, ex.getMessage());
        }
    }


    public void sendRegistrationSuccessEmail(String toEmail, String studentName, String eventTitle, String eventDate, String venue) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("projectfinalyear8685@gmail.com");
            message.setTo(toEmail);
            message.setSubject("✅ Registration Successful: " + eventTitle);

            message.setText(
                "Hello " + studentName + ",\n\n" +
                "You have successfully registered for the event: " + eventTitle + ".\n\n" +
                "📅 Date: " + eventDate + "\n" +
                "📍 Venue: " + venue + "\n\n" +
                "We look forward to seeing you there!\n\n" +
                "Regards,\n" +
                "College Event Management Team"
            );

            mailSender.send(message);
            logger.info("Registration success email sent to {}", toEmail);

        } catch (Exception ex) {
            logger.error("Failed to send registration email to {}: {}", toEmail, ex.getMessage());
        }
    }
}
