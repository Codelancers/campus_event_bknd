package com.finalyear.event.repository;

import com.finalyear.event.entity.EventRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends MongoRepository<EventRegistration, String> {
    List<EventRegistration> findByEventId(String eventId);
    List<EventRegistration> findByStudentId(String studentId);
    Optional<EventRegistration> findByEventIdAndStudentId(String eventId, String studentId);
    long countByEventId(String eventId);
}
