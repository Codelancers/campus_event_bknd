package com.finalyear.event.controller;

import com.finalyear.event.entity.Event;
import com.finalyear.event.payload.request.EventUpdateRequest;
import com.finalyear.event.payload.request.EventCreateRequest;
import com.finalyear.event.payload.response.ApiResponse;
import com.finalyear.event.service.EventService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")  // 🔥 Versioned & clean
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 🔥 CREATE EVENT
    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestParam String creatorId,
                                         @ModelAttribute EventCreateRequest request) {
        Event event = eventService.create(creatorId, request);
        return ResponseEntity.ok(new ApiResponse("Event created successfully", event));
    }

    // 🔥 UPDATE EVENT
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable String id,
            @ModelAttribute EventUpdateRequest request) {

        Event updated = eventService.updateEvent(id, request);
        return ResponseEntity.ok(new ApiResponse("Event updated successfully", updated));
    }

    


    // 🔥 CHANGE STATUS
    @PatchMapping("/{eventId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String eventId,
                                          @RequestParam String status) {
        Event updated = eventService.changeStatus(eventId, status);
        return ResponseEntity.ok(new ApiResponse("Event status updated", updated));
    }

    // 🔥 DELETE EVENT
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable String eventId) {
        eventService.delete(eventId);
        return ResponseEntity.ok(new ApiResponse("Event deleted", null));
    }

    // 🔥 LIST ALL EVENTS
    @GetMapping("/all")
    public ResponseEntity<?> getAllEvents() {
        List<Event> events = eventService.listAll();
        return ResponseEntity.ok(new ApiResponse("Events retrieved", events));
    }

    // 🔥 LIST BY DEPARTMENT (Optional Type)
    @GetMapping("/department/{department}")
    public ResponseEntity<?> getEventsByDepartment(
            @PathVariable String department,
            @RequestParam(required = false) Integer eventType) {

        List<Event> events;

        if (eventType != null) {
            events = eventService.listByDepartmentAndType(department, eventType);
        } else {
            events = eventService.listByDepartment(department);
        }

        return ResponseEntity.ok(new ApiResponse("Events retrieved", events));
    }
}
