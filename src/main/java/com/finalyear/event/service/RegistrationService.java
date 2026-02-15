package com.finalyear.event.service;

import com.finalyear.event.entity.Event;
import com.finalyear.event.entity.EventRegistration;
import com.finalyear.event.entity.User;
import com.finalyear.event.payload.request.RegistrationRequest;
import com.finalyear.event.payload.response.EventWithRegistrationStatusResponse;
import com.finalyear.event.payload.response.StudentRegistrationResponse;
import com.finalyear.event.payload.response.StudentEventsSummaryResponse;
import com.finalyear.event.payload.response.EventRegistrantResponse;
import com.finalyear.event.repository.EventRegistrationRepository;
import com.finalyear.event.repository.EventRepository;
import com.finalyear.event.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public RegistrationService(EventRegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               UserRepository userRepository,
                               EmailService emailService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
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
            throw new RuntimeException("Registration for this event has closed. Deadline: " + event.getRegistrationEndDate() + ", Today: " + LocalDate.now());
        }

        // 4. Check if already registered (by eventId and student)
        if (registrationRepository.findByEventIdAndStudentId(event.getEventId(), student.getId()).isPresent()) {
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
        // Store the public-facing eventId so lookups are consistent
        registration.setEventId(event.getEventId());
        registration.setStudentId(student.getId());
        registration.setStudentName(student.getName());
        registration.setStudentEmail(student.getEmail());
        registration.setDepartment(student.getDepartment());
        registration.setRollNo(student.getRollNo());
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

        // 9. Send Confirmation Email
        emailService.sendRegistrationSuccessEmail(
            student.getEmail(),
            student.getName(),
            event.getTitle(),
            event.getStartTime().toString(), // Or format date nicely
            event.getVenue()
        );

        return savedRegistration;
    }

    public List<EventRegistration> getRegistrationsByEvent(String eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<EventRegistrantResponse> getEventRegistrants(String eventId) {
        System.out.println("DEBUG: Fetching registrants for eventId: '" + eventId + "'");
        
        // 1. Get all registrations for the event
        List<EventRegistration> registrations = registrationRepository.findByEventId(eventId);
        System.out.println("DEBUG: Found " + registrations.size() + " registrations.");
        
        if (registrations.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        // 2. Extract student IDs
        List<String> studentIds = registrations.stream()
                .map(EventRegistration::getStudentId)
                .collect(Collectors.toList());
        System.out.println("DEBUG: Student IDs to fetch: " + studentIds);

        // 3. Fetch all students
        List<User> students = (List<User>) userRepository.findAllById(studentIds);
        System.out.println("DEBUG: Found " + students.size() + " student records.");
        
        Map<String, User> studentMap = students.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 4. Map to response DTO
        return registrations.stream()
                .map(reg -> {
                    User student = studentMap.get(reg.getStudentId());
                    
                    // Use User details if available, otherwise fallback to registration snapshot
                    if (student != null) {
                        return new EventRegistrantResponse(
                            reg.getId(),
                            student.getId(),
                            student.getRollNo(),
                            student.getName(),
                            student.getEmail(),
                            student.getPhone(),
                            student.getDepartment(),
                            student.getYear(),
                            reg.getStatus(),
                            reg.getRegisteredAt()
                        );
                    } else {
                        System.out.println("DEBUG: Student not found for ID: " + reg.getStudentId() + ", using snapshot.");
                        // Fallback using snapshot data stored in registration
                        // Note: Parsing Year safely if needed or using null
                        Integer yearInt = null;
                        try {
                            if (reg.getYear() != null && !reg.getYear().equals("null")) {
                                yearInt = Integer.parseInt(reg.getYear());
                            }
                        } catch (NumberFormatException e) {
                            // ignore
                        }
                        
                        return new EventRegistrantResponse(
                            reg.getId(),
                            reg.getStudentId(),
                            "N/A", // Roll number might not be in snapshot
                            reg.getStudentName(),
                            reg.getStudentEmail(),
                            "N/A", // Phone
                            reg.getDepartment(),
                            yearInt,
                            reg.getStatus(),
                            reg.getRegisteredAt()
                        );
                    }
                })
                .collect(Collectors.toList()); 
    }

    public List<EventRegistration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public List<EventRegistration> getRegistrationsByStudent(String studentId) {
        return registrationRepository.findByStudentId(studentId);
    }

    /**
     * Get all registrations for a student by roll number, including event details
     * @param rollNo The roll number of the student
     * @return List of StudentRegistrationResponse with event details
     */
    public List<StudentRegistrationResponse> getStudentRegistrationsWithEvents(String rollNo) {
        // Find student by roll number
        User student = userRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new RuntimeException("Student not found with Roll No: " + rollNo));

        // Get all registrations for this student
        List<EventRegistration> registrations = registrationRepository.findByStudentId(student.getId());

        // Map registrations to response DTOs with event details
        return registrations.stream()
                .map(registration -> {
                    Event event = eventRepository.findByEventId(registration.getEventId())
                            .orElse(null); // Skip if event not found

                    if (event == null) {
                        return null;
                    }

                    StudentRegistrationResponse response = new StudentRegistrationResponse();
                    response.setRegistrationId(registration.getId());
                    response.setEventId(event.getEventId());
                    response.setEventTitle(event.getTitle());
                    response.setEventDescription(event.getDescription());
                    response.setVenue(event.getVenue());
                    response.setStartTime(event.getStartTime());
                    response.setEndTime(event.getEndTime());
                    response.setDepartment(event.getDepartment());
                    response.setEventType(event.getEventType());
                    response.setStatus(registration.getStatus());
                    response.setRegisteredAt(registration.getRegisteredAt());
                    // response.setPoster(event.getPoster());
                    response.setMaxParticipants(event.getMaxParticipants());
                    response.setCurrentParticipants(event.getCount());
                    response.setRequirements(event.getRequirements());
                    response.setRegistrationEndDate(event.getRegistrationEndDate());
                    return response;
                })
                .filter(response -> response != null) // Filter out null responses
                .collect(Collectors.toList());
    }

    /**
     * Get all available events for a student's department with registration status
     * @param rollNo The roll number of the student
     * @return StudentEventsSummaryResponse with all events and registration status
     */
    public StudentEventsSummaryResponse getStudentEventsWithRegistrationStatus(String rollNo) {
        // Find student by roll number
        User student = userRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new RuntimeException("Student not found with Roll No: " + rollNo));

        // Get all available events for student's department (includes department-specific + "All" + null)
        LocalDate currentDate = LocalDate.now();
        List<Event> availableEvents = eventRepository.findEventsForDepartment(
                student.getDepartment(), 
                currentDate
        );

        // Get all registrations for this student
        List<EventRegistration> registrations = registrationRepository.findByStudentId(student.getId());

        // Create a map of event public IDs to registrations for quick lookup
        Map<String, EventRegistration> registrationMap = registrations.stream()
                .collect(Collectors.toMap(
                        EventRegistration::getEventId,
                        reg -> reg,
                        (existing, replacement) -> existing // Keep first if duplicate
                ));

        // Map events to response DTOs with registration status
        List<EventWithRegistrationStatusResponse> eventsWithStatus = availableEvents.stream()
                .map(event -> {
                    EventRegistration registration = registrationMap.get(event.getEventId());
                    
                    EventWithRegistrationStatusResponse response = new EventWithRegistrationStatusResponse();
                    response.setEventId(event.getEventId());
                    response.setEventTitle(event.getTitle());
                    response.setEventDescription(event.getDescription());
                    response.setVenue(event.getVenue());
                    response.setStartTime(event.getStartTime());
                    response.setEndTime(event.getEndTime());
                    response.setDepartment(event.getDepartment());
                    response.setEventType(event.getEventType());
                    // response.setPoster(event.getPoster());
                    response.setMaxParticipants(event.getMaxParticipants());
                    response.setCurrentParticipants(event.getCount());
                    response.setRequirements(event.getRequirements());
                    response.setRegistrationEndDate(event.getRegistrationEndDate());
                    response.setStatus(event.getStatus());
                    
                    // Set registration details if student is registered
                    if (registration != null) {
                        response.setRegistered(true);
                        response.setRegistrationId(registration.getId());
                        response.setRegistrationStatus(registration.getStatus());
                        response.setRegisteredAt(registration.getRegisteredAt());
                    } else {
                        response.setRegistered(false);
                        response.setRegistrationId(null);
                        response.setRegistrationStatus(null);
                        response.setRegisteredAt(null);
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());

        // Count registered events
        long registeredCount = eventsWithStatus.stream()
                .filter(EventWithRegistrationStatusResponse::isRegistered)
                .count();

        return new StudentEventsSummaryResponse(
                eventsWithStatus.size(),
                (int) registeredCount,
                student.getRollNo(),
                student.getDepartment(),
                eventsWithStatus
        );
    }
}
