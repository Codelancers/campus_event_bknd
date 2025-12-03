package com.finalyear.event.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.finalyear.event.entity.NotificationEntity;


public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {
}