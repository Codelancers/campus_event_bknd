package com.finalyear.event.payload.request;

import java.time.LocalDate;

import org.bson.types.Binary;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class EventUpdateRequest {

    private String title;
    private String description;
    private String venue;
    private String department;
    private Integer eventType;
    private MultipartFile poster;
    private String requirements;
    private LocalDate registrationEndDate;
    private Integer maxParticipants;
    private String status; // ACTIVE, COMPLETED, CANCELLED
}
