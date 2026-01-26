package com.finalyear.event.service;

import com.finalyear.event.entity.Event;
import com.finalyear.event.entity.User;
import com.finalyear.event.payload.request.EventUpdateRequest;
import com.finalyear.event.payload.request.EventCreateRequest;
import com.finalyear.event.repository.EventRepository;
import com.finalyear.event.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final com.finalyear.event.repository.EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    public EventService(EventRepository eventRepository,
                        SequenceGeneratorService sequenceGeneratorService,
                        NotificationService notificationService,
                        UserRepository userRepository,
                        EmailService emailService,
                        com.finalyear.event.repository.EventRegistrationRepository eventRegistrationRepository) {
        this.eventRepository = eventRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.eventRegistrationRepository = eventRegistrationRepository;
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
                request.getMaxParticipants() != null ? request.getMaxParticipants() : -1
        );

        event.setCount(0);
        event.setCreatedBy(creatorId);
        event.setStatus("SCHEDULED");
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

        // ==========================
        // POSTER UPLOAD
        // ==========================
        try {
            if (request.getPoster() != null && !request.getPoster().isEmpty()) {
                event.setPoster(new Binary(request.getPoster().getBytes()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing poster image", e);
        }

        Event saved = eventRepository.save(event);

        // ==========================
        // TARGET USER LOGIC
        // ==========================
        // ==========================
        // TARGET USER LOGIC
        // ==========================
        boolean isAllDepartments =
                request.getDepartment() == null ||
                request.getDepartment().isBlank() ||
                request.getDepartment().equalsIgnoreCase("ALL");

        if (isAllDepartments) {
            // 🔔 In-app notification (ALL)
            notificationService.sendEventNotification(saved);
        } else {
            // 🔔 In-app notification (Department)
            notificationService.sendDepartmentNotification(
                    request.getDepartment(), saved
            );
        }

        // ==========================
        // EMAIL NOTIFICATIONS (ASYNC)
        // ==========================
        notificationService.sendEventCreatedEmails(saved);

        return saved;
    }



    // UPDATE EVENT
    public Event updateEvent(String id, EventUpdateRequest request) {

        Event event = eventRepository.findById(id)
                .or(() -> eventRepository.findByEventId(id))
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

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


    public Event declareWinner(String eventId, String rollNo) {
        // 1. Find Event (support both MongoID and Business Key)
        Event event = eventRepository.findById(eventId)
                .or(() -> eventRepository.findByEventId(eventId))
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // 2. Find Student by Roll No
        User student = userRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new RuntimeException("Student not found with Roll No: " + rollNo));

        // 3. Find Registration (Using Business Key event.getEventId())
        com.finalyear.event.entity.EventRegistration registration = eventRegistrationRepository.findByEventIdAndStudentId(event.getEventId(), student.getId())
                .orElseThrow(() -> new RuntimeException("Student with Roll No " + rollNo + " is not registered for event " + event.getEventId()));

        // 4. Update Event (Add Winner)
        if (event.getWinnerIds() == null) {
            event.setWinnerIds(new java.util.ArrayList<>());
        }
        if (!event.getWinnerIds().contains(student.getRollNo())) {
            event.getWinnerIds().add(student.getRollNo());
            eventRepository.save(event);
        }

        // 5. Update Registration Status
        registration.setStatus("WINNER");
        eventRegistrationRepository.save(registration);

        // 6. Update User (Add won event)
        if (student.getWonEvents() == null) {
            student.setWonEvents(new java.util.ArrayList<>());
        }
        if (!student.getWonEvents().contains(event.getId())) {
            student.getWonEvents().add(event.getId());
            
            // Optionally add points for winning?
             if (student.getPoints() == null) student.setPoints(0);
             student.setPoints(student.getPoints() + 10); // Example: 10 points for winning

            userRepository.save(student);
        }

        return event;
    }
}

