package com.finalyear.event.controller;

import com.finalyear.event.entity.Event;
import com.finalyear.event.payload.request.EventRequest;
import com.finalyear.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Create event
    @PostMapping
    public ResponseEntity<?> create(@RequestParam String creatorId,
                                    @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.create(creatorId, request));
    }

    // Update event
    @PutMapping("/{eventId}")
    public ResponseEntity<?> update(@PathVariable String eventId,
                                    @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.update(eventId, request));
    }

    // Change event status
    @PutMapping("/{eventId}/status")
    public ResponseEntity<?> changeStatus(@PathVariable String eventId,
                                          @RequestParam String status) {
        return ResponseEntity.ok(eventService.changeStatus(eventId, status));
    }

    // Delete event
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> delete(@PathVariable String eventId) {
        eventService.delete(eventId);
        return ResponseEntity.ok("Event deleted");
    }

    // List all events
    @GetMapping
    public ResponseEntity<List<Event>> list() {
        return ResponseEntity.ok(eventService.listAll());
    }
}
