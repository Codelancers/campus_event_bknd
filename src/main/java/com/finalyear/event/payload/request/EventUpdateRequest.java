package com.finalyear.event.payload.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class EventUpdateRequest {

    private String title;
    private String description;
    private String venue;
    private LocalDate eventDate;
    private String department;
    private Integer maxParticipants;
    private String status; // ACTIVE, COMPLETED, CANCELLED
}
