package com.finalyear.event.entity;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.bson.types.Binary;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Data;


@Data
@Document(collection = "events")
public class Event {
    public static final String SEQUENCE_NAME = "events_sequence";
    
    @Id
    private String id;
    private String eventId;
    private String title;
    private String description;
    private String department;
    private String venue;
    private Integer eventType; // 0: Technical, 1: Cultural
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer count; // New field
    private String coverImageUrl;
    private Binary poster;
    private String requirements;
    private LocalDate registrationEndDate;
    private String createdBy; // user id
    private List<String> winnerIds;
    private String status; // SCHEDULED, ONGOING, COMPLETED, CANCELLED
    private Instant createdAt;
    private Instant updatedAt;
}