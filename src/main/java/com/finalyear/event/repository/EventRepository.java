package com.finalyear.event.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.finalyear.event.entity.Event;


public interface EventRepository extends MongoRepository<Event, String> {
}