package com.finalyear.event.controller;

import com.finalyear.event.entity.NotificationEntity;
import com.finalyear.event.payload.request.NotificationRequest;
import com.finalyear.event.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Create + broadcast notification
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.create(request));
    }

    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        notificationService.delete(id);
        return ResponseEntity.ok("Notification deleted");
    }

    // Get all notifications
    @GetMapping
    public ResponseEntity<List<NotificationEntity>> list() {
        return ResponseEntity.ok(notificationService.listAll());
    }
}
