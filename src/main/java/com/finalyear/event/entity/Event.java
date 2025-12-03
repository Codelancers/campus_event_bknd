package com.finalyear.event.entity;


import java.time.Instant;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Data;


@Data
@Document(collection = "events")
public class Event {
@Id
private String id;
private String title;
private String description;
private String department;
private String venue;
private Instant startTime;
private Instant endTime;
private Integer maxParticipants;
private String coverImageUrl;
private String createdBy; // user id
private List<String> winnerIds;
private String status; // SCHEDULED, ONGOING, COMPLETED, CANCELLED
private Instant createdAt;
private Instant updatedAt;
}