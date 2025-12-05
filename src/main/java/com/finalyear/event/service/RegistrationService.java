package com.finalyear.event.service;

import com.finalyear.event.entity.Event;
import com.finalyear.event.entity.EventRegistration;
import com.finalyear.event.entity.User;
import com.finalyear.event.payload.request.RegistrationRequest;
import com.finalyear.event.repository.EventRegistrationRepository;
import com.finalyear.event.repository.EventRepository;
import com.finalyear.event.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class RegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RegistrationService(EventRegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }
    public EventRegistration registerStudent(RegistrationRequest request) {
        // 1. Check if event exists
        Event event = eventRepository.findByEventId(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. Check if student exists by Roll No
        User student = userRepository.findByRollNo(request.getRollNo())
                .orElseThrow(() -> new RuntimeException("Student not found with Roll No: " + request.getRollNo()));

        // 3. Check if registration is open (date check)
        if (event.getRegistrationEndDate() != null && LocalDate.now().isAfter(event.getRegistrationEndDate())) {
            throw new RuntimeException("Registration for this event has closed.");
        }

        // 4. Check if already registered
        if (registrationRepository.findByEventIdAndStudentId(request.getEventId(), student.getId()).isPresent()) {
            throw new RuntimeException("Student is already registered for this event.");
        }

        // 5. Check max participants limit
        if (event.getMaxParticipants() != null && event.getMaxParticipants() != -1) {
            long currentCount = registrationRepository.countByEventId(request.getEventId());
            if (currentCount >= event.getMaxParticipants()) {
                throw new RuntimeException("Event is full.");
            }
        }

        // 6. Create Registration
        EventRegistration registration = new EventRegistration();
        registration.setEventId(event.getId());
        registration.setStudentId(student.getId());
        registration.setStudentName(student.getName());
        registration.setStudentEmail(student.getEmail());
        registration.setDepartment(student.getDepartment());
        registration.setEventType(event.getEventType() != null ? event.getEventType().toString() : null);
        
        registration.setStatus("REGISTERED");
        registration.setRegisteredAt(Instant.now());

        EventRegistration savedRegistration = registrationRepository.save(registration);

        // 7. Update User Points (Add 2 points)
        if (student.getPoints() == null) {
            student.setPoints(0);
        }
        student.setPoints(student.getPoints() + 2);
        
        // Add event to student's registered list
        if (student.getRegisteredEvents() == null) {
            student.setRegisteredEvents(new java.util.ArrayList<>());
        }
        student.getRegisteredEvents().add(event.getId());
        
        userRepository.save(student);

        // 8. Update Event Count
        if (event.getCount() == null) {
            event.setCount(0);
        }
        event.setCount(event.getCount() + 1);
        eventRepository.save(event);

        return savedRegistration;
    }

    public List<EventRegistration> getRegistrationsByEvent(String eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<EventRegistration> getRegistrationsByStudent(String studentId) {
        return registrationRepository.findByStudentId(studentId);
    }
}
