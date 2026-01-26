package com.finalyear.event.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegistrationsSummaryResponse {
    private int totalRegistrations;
    private List<StudentRegistrationResponse> registrations;
}

