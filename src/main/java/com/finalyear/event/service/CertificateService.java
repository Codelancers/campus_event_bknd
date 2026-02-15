package com.finalyear.event.service;

import com.finalyear.event.entity.Certificate;
import com.finalyear.event.entity.Event;
import com.finalyear.event.entity.User;
import com.finalyear.event.repository.CertificateRepository;
import com.finalyear.event.repository.EventRepository;
import com.finalyear.event.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public CertificateService(CertificateRepository certificateRepository,
                              EventRepository eventRepository,
                              UserRepository userRepository) {
        this.certificateRepository = certificateRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Certificate generateCertificate(String eventId, String rollNo) {
        // 1. Validate Event
        Event event = eventRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        // 2. Validate Student
        User student = userRepository.findByRollNo(rollNo)
                .orElseThrow(() -> new RuntimeException("Student not found with Roll No: " + rollNo));

        // 3. Check if certificate already exists
        if (certificateRepository.findByEventIdAndRollNo(event.getEventId(), student.getRollNo()).isPresent()) {
            throw new RuntimeException("Certificate already issued for this student and event.");
        }

        // 4. Create Certificate
        Certificate certificate = new Certificate();
        certificate.setEventId(event.getEventId());
        certificate.setEventName(event.getTitle());
        certificate.setRollNo(student.getRollNo());
        certificate.setStudentName(student.getName());
        certificate.setDepartment(student.getDepartment());
        certificate.setIssuedAt(Instant.now());
        
        // Generate a unique code (using UUID for global uniqueness and security)
        String uniqueCode = "CERT-" + UUID.randomUUID().toString().toUpperCase();
        certificate.setCertificateCode(uniqueCode);

        return certificateRepository.save(certificate);
    }

    public List<Certificate> getCertificatesByStudent(String rollNo) {
        return certificateRepository.findByRollNo(rollNo);
    }

    public Certificate getCertificateByCode(String code) {
        return certificateRepository.findByCertificateCode(code)
                .orElseThrow(() -> new RuntimeException("Certificate not found with code: " + code));
    }

    public Certificate getCertificateByEventAndRollNo(String eventId, String rollNo) {
        // Resolve canonical event ID first (in case user passed ObjectId)
        com.finalyear.event.entity.Event event = eventRepository.findById(eventId)
                .or(() -> eventRepository.findByEventId(eventId))
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));
        
        return certificateRepository.findByEventIdAndRollNo(event.getEventId(), rollNo)
                .orElseThrow(() -> new RuntimeException("Certificate not found for this event and student."));
    }
}
