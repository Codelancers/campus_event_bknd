package com.finalyear.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "event_registrations")
public class EventRegistration {

    @Id
    private String id;

    private String eventId;
    private String studentId; // User ID of the student
    private String studentName;
    private String studentEmail;
    private String department;
    private String year;
    private String eventType;
    
    private String status; // REGISTERED, CANCELLED, ATTENDED, WINNER
    
    private Instant registeredAt;
}
