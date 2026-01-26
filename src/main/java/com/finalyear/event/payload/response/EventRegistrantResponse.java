package com.finalyear.event.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrantResponse {
    private String registrationId;
    private String studentId;
    private String rollNo;
    private String name;
    private String email;
    private String phone;
    private String department;
    private Integer year;
    private String status;
    private Instant registeredAt;
}
