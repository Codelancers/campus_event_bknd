package com.finalyear.event.service;

import com.finalyear.event.entity.NotificationEntity;
import com.finalyear.event.payload.request.NotificationRequest;
import com.finalyear.event.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
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
        messagingTemplate.convertAndSend("/topic/events", payload);
    }

    public void sendDepartmentNotification(String department, Object payload) {
        messagingTemplate.convertAndSend("/topic/events/" + department, payload);
    }
}
