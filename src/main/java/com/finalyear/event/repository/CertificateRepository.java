package com.finalyear.event.repository;

import com.finalyear.event.entity.Certificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends MongoRepository<Certificate, String> {
    List<Certificate> findByEventId(String eventId);
    List<Certificate> findByRollNo(String rollNo);
    Optional<Certificate> findByCertificateCode(String certificateCode);
    Optional<Certificate> findByEventIdAndRollNo(String eventId, String rollNo); // Check if certificate already exists
}
