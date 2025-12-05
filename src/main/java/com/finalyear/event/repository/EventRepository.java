package com.finalyear.event.repository;


import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.finalyear.event.entity.Event;


public interface EventRepository extends MongoRepository<Event, String> {
    
    @Query("{ 'registrationEndDate': { $gte: ?1 }, $or: [ { 'department': ?0 }, { 'department': 'All' }, { 'department': null } ] }")
    List<Event> findEventsForDepartment(String department, LocalDate currentDate);

    @Query("{ 'registrationEndDate': { $gte: ?2 }, 'eventType': ?1, $or: [ { 'department': ?0 }, { 'department': 'All' }, { 'department': null } ] }")
    List<Event> findEventsForDepartmentAndType(String department, Integer eventType, LocalDate currentDate);

    java.util.Optional<Event> findByEventId(String eventId);
}