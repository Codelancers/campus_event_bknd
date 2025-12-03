package com.finalyear.event.service;

import com.finalyear.event.entity.Event;
import com.finalyear.event.payload.request.EventRequest;
import com.finalyear.event.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event create(String creatorId, EventRequest request) {
        Event event = new Event();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDepartment(request.getDepartment());
        event.setVenue(request.getVenue());
        event.setStartTime(Instant.parse(request.getStartTime()));
        event.setEndTime(Instant.parse(request.getEndTime()));
        event.setMaxParticipants(request.getMaxParticipants());
        event.setCoverImageUrl(request.getCoverImageUrl());
        event.setCreatedBy(creatorId);
        event.setStatus("SCHEDULED");

        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

        return eventRepository.save(event);
    }

    public Event update(String eventId, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getDepartment() != null) event.setDepartment(request.getDepartment());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getStartTime() != null) event.setStartTime(Instant.parse(request.getStartTime()));
        if (request.getEndTime() != null) event.setEndTime(Instant.parse(request.getEndTime()));
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
}
