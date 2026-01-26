package com.finalyear.event.controller;

import com.finalyear.event.entity.EventRegistration;
import com.finalyear.event.payload.request.RegistrationRequest;
import com.finalyear.event.payload.response.StudentEventsSummaryResponse;
import com.finalyear.event.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyForEvent(@RequestBody RegistrationRequest request) {
        try {
            return ResponseEntity.ok(registrationService.registerStudent(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<com.finalyear.event.payload.response.EventRegistrantResponse>> getEventRegistrations(@PathVariable String eventId) {
        return ResponseEntity.ok(registrationService.getEventRegistrants(eventId));
    }

    @GetMapping("/debug/all")
    public ResponseEntity<List<EventRegistration>> getAllRegistrationsDebug() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    @GetMapping("/student/{rollNo}")
    public ResponseEntity<?> getStudentRegistrations(@PathVariable String rollNo) {
        try {
            // Return all available events with registration status
            StudentEventsSummaryResponse response = registrationService.getStudentEventsWithRegistrationStatus(rollNo);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
