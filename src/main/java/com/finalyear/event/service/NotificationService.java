package com.finalyear.event.service;

import com.finalyear.event.entity.NotificationEntity;
import com.finalyear.event.payload.request.NotificationRequest;
import com.finalyear.event.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
}
