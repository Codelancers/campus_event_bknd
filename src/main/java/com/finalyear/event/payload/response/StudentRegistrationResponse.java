package com.finalyear.event.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationResponse {
    private String registrationId;
    private String eventId;
    private String eventTitle;
    private String eventDescription;
    private String venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String department;
    private Integer eventType; // 0: Technical, 1: Cultural
    private String status; // REGISTERED, CANCELLED, ATTENDED, WINNER
    private Instant registeredAt;
//    // private org.bson.types.Binary poster; // Was coverImageUrl
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String requirements;
    private LocalDate registrationEndDate;
}

