package com.finalyear.event.entity;


import java.time.Instant;
import java.util.List;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import lombok.Data;


@Data
@Document(collection = "admins")
public class Admin {
@Id
private String id;
private String name;
private String email;
private String otpHash; // bcrypt hashed OTP
private Instant otpExpiry;
private List<String> roles;
private Integer adminType;
private Instant createdAt;
private Instant updatedAt;
}