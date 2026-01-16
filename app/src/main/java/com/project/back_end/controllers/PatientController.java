package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;
        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@Valid @RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();
        if (!service.validatePatient(patient)) {
            response.put("error", "Patient with this email or phone already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put("message", "Patient registered successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("error", "Error registering patient");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/appointments/{patientId}/{token}/{user}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long patientId, @PathVariable String token,
                                                   @PathVariable String user) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (validation != null) return validation;
        return patientService.getPatientAppointment(patientId);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(@PathVariable String condition,
                                                                         @PathVariable String name,
                                                                         @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid or expired token");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        return service.filterPatient(token, condition, name);
    }
}
