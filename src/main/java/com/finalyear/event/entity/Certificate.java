package com.finalyear.event.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "certificates")
public class Certificate {

    @Id
    private String id;

    private String eventId;
    private String eventName;
    private String rollNo;
    private String studentName;
    private String department;
    
    // Unique code for verification, e.g., CERT-EVENT001-ROLL123
    private String certificateCode; 
    
    private Instant issuedAt;
}
