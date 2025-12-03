package com.finalyear.event.payload.request;


import lombok.Data;


@Data
public class EventRequest {
private String title;
private String description;
private String department;
private String venue;
private String startTime; // ISO instant
private String endTime;
private Integer maxParticipants;
private String coverImageUrl;
}