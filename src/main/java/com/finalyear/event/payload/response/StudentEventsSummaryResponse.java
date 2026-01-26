package com.finalyear.event.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEventsSummaryResponse {
    private int totalAvailableEvents;
    private int totalRegisteredEvents;
    private String studentRollNo;
    private String studentDepartment;
    private List<EventWithRegistrationStatusResponse> events;
}

