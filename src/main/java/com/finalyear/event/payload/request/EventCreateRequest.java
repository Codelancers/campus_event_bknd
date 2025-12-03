package com.finalyear.event.payload.request;

import java.time.LocalDate;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventCreateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String venue;

    @NotNull
    private LocalDate eventDate;

    private String department;     // Optional filter: CSE, ECE, etc.

    private List<String> skillTags; // e.g. ["AI", "Cloud", "Robotics"]

    private Integer maxParticipants;
}
