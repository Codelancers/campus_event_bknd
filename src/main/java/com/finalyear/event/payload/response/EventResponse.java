package com.finalyear.event.payload.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Data
public class EventResponse {

    private String id;
    private String title;
    private String description;
    private String venue;
    private LocalDate eventDate;
    private String department;
    private String createdBy;

    private Integer maxParticipants;
    private Integer registeredCount;

    private List<String> skillTags;
    private String status;

    private Instant createdAt;
}
