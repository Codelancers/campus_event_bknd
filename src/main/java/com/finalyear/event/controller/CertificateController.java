package com.finalyear.event.controller;

import com.finalyear.event.entity.Certificate;
import com.finalyear.event.payload.response.ApiResponse;
import com.finalyear.event.service.CertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    // GENERATE CERTIFICATE
    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@RequestParam String eventId, 
                                                 @RequestParam String rollNo) {
        try {
            Certificate certificate = certificateService.generateCertificate(eventId, rollNo);
            return ResponseEntity.ok(new ApiResponse("Certificate generated successfully", certificate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // GET CERTIFICATES BY STUDENT ROLL NO
    @GetMapping("/student/{rollNo}")
    public ResponseEntity<?> getCertificatesByStudent(@PathVariable String rollNo) {
        List<Certificate> certificates = certificateService.getCertificatesByStudent(rollNo);
        return ResponseEntity.ok(new ApiResponse("Certificates retrieved", certificates));
    }

    // VERIFY / GET CERTIFICATE BY CODE
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getCertificateByCode(@PathVariable String code) {
        try {
            Certificate certificate = certificateService.getCertificateByCode(code);
            return ResponseEntity.ok(new ApiResponse("Certificate found", certificate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    // GET CERTIFICATE BY EVENT ID AND ROLL NO
    @GetMapping("/event/{eventId}/student/{rollNo}")
    public ResponseEntity<?> getCertificateByEventAndStudent(@PathVariable String eventId,
                                                             @PathVariable String rollNo) {
        try {
            Certificate certificate = certificateService.getCertificateByEventAndRollNo(eventId, rollNo);
            return ResponseEntity.ok(new ApiResponse("Certificate retrieved successfully", certificate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
