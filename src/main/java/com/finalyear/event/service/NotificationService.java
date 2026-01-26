package com.finalyear.event.service;

import com.finalyear.event.entity.NotificationEntity;
import com.finalyear.event.payload.request.NotificationRequest;
import com.finalyear.event.entity.Event;
import com.finalyear.event.entity.User;
import com.finalyear.event.repository.NotificationRepository;
import com.finalyear.event.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate,
                               UserRepository userRepository,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public NotificationEntity create(NotificationRequest request) {
        NotificationEntity n = new NotificationEntity();

        n.setTitle(request.getTitle());
        n.setMessage(request.getMessage());
        n.setEventId(request.getEventId());

        NotificationEntity.Target target = new NotificationEntity.Target();
        target.setDepartment(request.getDepartment());
        target.setYear(request.getYear());
        n.setTarget(target);

        n.setCreatedAt(Instant.now());
        n.setSentAt(Instant.now());

        return notificationRepository.save(n);
    }

    public void delete(String id) {
        notificationRepository.deleteById(id);
    }

    public List<NotificationEntity> listAll() {
        return notificationRepository.findAll();
    }

    public void sendEventNotification(Object payload) {
        try {
            logger.info("Sending event notification to /topic/events");
            logger.debug("Event payload: {}", payload);
            messagingTemplate.convertAndSend("/topic/events", payload);
            logger.info("Event notification sent successfully to /topic/events");
        } catch (Exception e) {
            logger.error("Error sending event notification to /topic/events", e);
            throw new RuntimeException("Failed to send WebSocket notification", e);
        }
    }

    public void sendDepartmentNotification(String department, Object payload) {
        try {
            // Normalize department name to uppercase for consistency
            String normalizedDept = department != null ? department.toUpperCase() : "ALL";
            String topic = "/topic/events/" + normalizedDept;
            logger.info("Sending department notification to: {}", topic);
            logger.debug("Department: {} (normalized: {}), Event payload: {}", department, normalizedDept, payload);
            messagingTemplate.convertAndSend(topic, payload);
            logger.info("Department notification sent successfully to: {}", topic);
        } catch (Exception e) {
            logger.error("Error sending department notification to /topic/events/{}", department, e);
            throw new RuntimeException("Failed to send WebSocket notification", e);
        }
    }

    @Async
    public void sendEventCreatedEmails(Event event) {
        String department = event.getDepartment();
        boolean isAllDepartments = department == null || department.isBlank() || department.equalsIgnoreCase("ALL");

        List<User> targetUsers;
        if (isAllDepartments) {
            targetUsers = userRepository.findAll();
        } else {
            targetUsers = userRepository.findByDepartment(department);
        }

        logger.info("Starting async email sending for event: {} to {} users", event.getTitle(), targetUsers.size());
        
        for (User user : targetUsers) {
            try {
                emailService.sendEventCreatedEmail(
                    user.getEmail(),
                    event.getTitle(),
                    isAllDepartments ? "All Departments" : user.getDepartment()
                );
            } catch (Exception e) {
                logger.error("Failed to send email to user: {}", user.getEmail(), e);
            }
        }
        logger.info("Finished async email sending for event: {}", event.getTitle());
    }
}
