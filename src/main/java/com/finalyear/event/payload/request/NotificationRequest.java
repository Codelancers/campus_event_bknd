package com.finalyear.event.payload.request;


import lombok.Data;


@Data
public class NotificationRequest {
private String title;
private String message;
private String eventId;
private String department; // null = all
private Integer year; // null = all
}