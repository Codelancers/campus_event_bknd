package com.finalyear.event.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWithRegistrationStatusResponse {
    private String eventId;
    private String eventTitle;
    private String eventDescription;
    private String venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String department;
    private Integer eventType; // 0: Technical, 1: Cultural
    // private org.bson.types.Binary poster; // Was coverImageUrl
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String requirements;
    private LocalDate registrationEndDate;
    private String status; // SCHEDULED, ONGOING, COMPLETED, CANCELLED
    private boolean isRegistered;
    private String registrationId; // null if not registered
    private String registrationStatus; // REGISTERED, CANCELLED, ATTENDED, WINNER - null if not registered
    private java.time.Instant registeredAt; // null if not registered
}

