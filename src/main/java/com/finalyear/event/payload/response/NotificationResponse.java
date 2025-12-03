package com.finalyear.event.payload.response;

import lombok.Data;
import java.time.Instant;

@Data
public class NotificationResponse {

    private String id;
    private String title;
    private String message;
    private String eventId;
    private Instant createdAt;
    private String createdBy;
}
