package com.finalyear.event.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private String department;
    private Integer year;
    private String rollNo;
    private List<String> roles;
    private String otpHash = "";
    private Instant otpExpiry = null; // this is okay
    private Integer points;
    private List<String> registeredEvents = new ArrayList<>();
    private List<String> wonEvents = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}