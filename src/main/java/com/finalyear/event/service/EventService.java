package com.finalyear.event.service;

import com.finalyear.event.entity.Event;
import com.finalyear.event.payload.request.EventRequest;
import com.finalyear.event.payload.request.EventCreateRequest;
import com.finalyear.event.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.io.IOException;
import org.bson.types.Binary;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final NotificationService notificationService;

    public EventService(EventRepository eventRepository, SequenceGeneratorService sequenceGeneratorService, NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.notificationService = notificationService;
    }

    public Event create(String creatorId, EventCreateRequest request) {
        Event event = new Event();
        
        event.setEventId("EVENT" + String.format("%03d", sequenceGeneratorService.generateSequence(Event.SEQUENCE_NAME)));
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDepartment(request.getDepartment());
        event.setEventType(request.getEventType());
        event.setVenue(request.getVenue());
        
        event.setStartTime(request.getEventStartTime());
        event.setEndTime(request.getEventEndTime());
        event.setRegistrationEndDate(request.getRegistrationEndDate());
        
        if (request.getMaxParticipants() != null) {
            event.setMaxParticipants(request.getMaxParticipants());
        } else {
            event.setMaxParticipants(-1); // -1 indicates unlimited
        }
        
        event.setCount(0);
        
        try {
            if (request.getPoster() != null && !request.getPoster().isEmpty()) {
                event.setPoster(new Binary(request.getPoster().getBytes()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing poster image", e);
        }

        event.setRequirements(request.getRequirements());
        
        event.setCreatedBy(creatorId);
        event.setStatus("SCHEDULED");

        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

        Event savedEvent = eventRepository.save(event);

        // Send notification
        if (savedEvent.getDepartment() == null || savedEvent.getDepartment().isEmpty() || "All".equalsIgnoreCase(savedEvent.getDepartment())) {
            notificationService.sendEventNotification(savedEvent);
        } else {
            notificationService.sendDepartmentNotification(savedEvent.getDepartment(), savedEvent);
        }

        return savedEvent;
    }

    public Event update(String eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getDepartment() != null) event.setDepartment(request.getDepartment());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getStartTime() != null) event.setStartTime(java.time.LocalDateTime.parse(request.getStartTime()));
        if (request.getEndTime() != null) event.setEndTime(java.time.LocalDateTime.parse(request.getEndTime()));
        if (request.getMaxParticipants() != null) event.setMaxParticipants(request.getMaxParticipants());
        if (request.getCoverImageUrl() != null) event.setCoverImageUrl(request.getCoverImageUrl());

        event.setUpdatedAt(Instant.now());
        return eventRepository.save(event);
    }

    public Event changeStatus(String eventId, String status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        event.setStatus(status);
        event.setUpdatedAt(Instant.now());

        return eventRepository.save(event);
    }

    public void delete(String eventId) {
        eventRepository.deleteById(eventId);
    }

    public List<Event> listAll() {
        return eventRepository.findAll();
    }

    public List<Event> listByDepartment(String department) {
        return eventRepository.findEventsForDepartment(department, java.time.LocalDate.now());
    }

    public List<Event> listByDepartmentAndType(String department, Integer eventType) {
        return eventRepository.findEventsForDepartmentAndType(department, eventType, java.time.LocalDate.now());
    }
}
