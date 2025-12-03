package com.finalyear.event.entity;


import java.time.Instant;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Data;


@Data
@Document(collection = "notifications")
public class NotificationEntity {
@Id
private String id;
private String title;
private String message;
private String eventId;
private Target target;
private Instant createdAt;
private Instant sentAt;
private List<String> readBy;


@Data
public static class Target {
private String department; // null = all
private Integer year; // null = all
}
}