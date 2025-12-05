package com.finalyear.event.payload.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
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
    private LocalDate registrationEndDate;

    @NotNull
    private LocalDateTime eventStartTime;

    @NotNull
    private LocalDateTime eventEndTime;

    private String department;     // Optional filter: CSE, ECE, etc.
    
    private Integer eventType; // 0: Technical, 1: Cultural

    private List<String> skillTags; // e.g. ["AI", "Cloud", "Robotics"]

    private Integer maxParticipants;
    private Integer count;

    private String requirements;

    private MultipartFile poster;
}
