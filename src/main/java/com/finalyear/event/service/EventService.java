package com.finalyear.event.service;

import com.finalyear.event.entity.Event;
import com.finalyear.event.payload.request.EventUpdateRequest;
import com.finalyear.event.payload.request.EventCreateRequest;
import com.finalyear.event.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.io.IOException;
import org.bson.types.Binary;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final NotificationService notificationService;

    public EventService(EventRepository eventRepository,
                        SequenceGeneratorService sequenceGeneratorService,
                        NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.notificationService = notificationService;
    }

    // CREATE EVENT
    public Event create(String creatorId, EventCreateRequest request) {

        Event event = new Event();

        event.setEventId("EVENT" + String.format("%03d",
                sequenceGeneratorService.generateSequence(Event.SEQUENCE_NAME)));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setVenue(request.getVenue());
        event.setDepartment(request.getDepartment());
        event.setEventType(request.getEventType());
        event.setRequirements(request.getRequirements());

        event.setStartTime(request.getEventStartTime());
        event.setEndTime(request.getEventEndTime());
        event.setRegistrationEndDate(request.getRegistrationEndDate());

        event.setMaxParticipants(
                request.getMaxParticipants() != null
                        ? request.getMaxParticipants()
                        : -1
        );

        event.setCount(0);

        // Handle poster upload
        try {
            if (request.getPoster() != null && !request.getPoster().isEmpty()) {
                event.setPoster(new Binary(request.getPoster().getBytes()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing poster image", e);
        }

        event.setCreatedBy(creatorId);
        event.setStatus("SCHEDULED");
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

        Event saved = eventRepository.save(event);

        // Notifications
        if (saved.getDepartment() == null ||
            saved.getDepartment().equalsIgnoreCase("All") ||
            saved.getDepartment().isEmpty()) {

            notificationService.sendEventNotification(saved);

        } else {
            notificationService.sendDepartmentNotification(saved.getDepartment(), saved);
        }

        return saved;
    }

    // UPDATE EVENT
    public Event updateEvent(String id, EventUpdateRequest request) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getDepartment() != null) event.setDepartment(request.getDepartment());
        if (request.getEventType() != null) event.setEventType(request.getEventType());
        if (request.getRequirements() != null) event.setRequirements(request.getRequirements());
        if (request.getRegistrationEndDate() != null)
            event.setRegistrationEndDate(request.getRegistrationEndDate());
        if (request.getMaxParticipants() != null)
            event.setMaxParticipants(request.getMaxParticipants());
        if (request.getStatus() != null) event.setStatus(request.getStatus());

        // 🔥 Handle poster upload (optional)
        if (request.getPoster() != null && !request.getPoster().isEmpty()) {
            try {
                Binary posterBinary = new Binary(request.getPoster().getBytes());
                event.setPoster(posterBinary);
            } catch (IOException e) {
                throw new RuntimeException("Failed to process poster image", e);
            }
        }


        event.setUpdatedAt(Instant.now());

        return eventRepository.save(event);
    }


    // CHANGE STATUS
    public Event changeStatus(String id, String status) {
        Event event = getEventOrThrow(id);
        event.setStatus(status);
        event.setUpdatedAt(Instant.now());
        return eventRepository.save(event);
    }


    // DELETE EVENT
    public void delete(String id) {
        getEventOrThrow(id); // ensures event exists
        eventRepository.deleteById(id);
    }


    // LIST ALL EVENTS
    public List<Event> listAll() {
        return eventRepository.findAll();
    }

    // FILTER BY DEPARTMENT
    public List<Event> listByDepartment(String department) {
        return eventRepository.findEventsForDepartment(department, LocalDate.now());
    }

    // FILTER BY DEPARTMENT + TYPE
    public List<Event> listByDepartmentAndType(String department, Integer eventType) {
        return eventRepository.findEventsForDepartmentAndType(department, eventType, LocalDate.now());
    }

    public Event getEventOrThrow(String id) {
    return eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
}

}

