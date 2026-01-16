package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import com.project.back_end.services.TokenService;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.PatientRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;
    private final PatientService patientService;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;

    public AppointmentController(AppointmentService appointmentService, Service service,
                                  PatientService patientService, TokenService tokenService,
                                  PatientRepository patientRepository) {
        this.appointmentService = appointmentService;
        this.service = service;
        this.patientService = patientService;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/{doctorId}/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable Long doctorId, @PathVariable String date,
                                              @PathVariable String patientName, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation != null) return validation;

        LocalDate localDate = LocalDate.parse(date);
        String name = "null".equals(patientName) ? null : patientName;
        return appointmentService.getAppointments(doctorId, localDate, name);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@Valid @RequestBody Appointment appointment,
                                                                @PathVariable String token) {
        Map<String, String> response = new HashMap<>();
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;

        int validationResult = service.validateAppointment(appointment.getDoctor().getId(),
                appointment.getAppointmentTime().toLocalTime());
        if (validationResult == -1) {
            response.put("error", "Invalid doctor ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } else if (validationResult == 0) {
            response.put("error", "Time slot not available");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            response.put("message", "Appointment booked successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("error", "Error booking appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{appointmentId}/{doctorId}/{token}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long appointmentId, @PathVariable Long doctorId,
                                                @RequestBody Map<String, String> body, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;

        LocalDateTime newTime = LocalDateTime.parse(body.get("appointmentTime"));
        return appointmentService.updateAppointment(appointmentId, doctorId, newTime, token);
    }

    @DeleteMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long appointmentId,
                                                                  @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;
        return appointmentService.cancelAppointment(appointmentId, token);
    }

    @PutMapping("/status/{appointmentId}/{status}/{token}")
    public ResponseEntity<Map<String, String>> changeStatus(@PathVariable Long appointmentId,
                                                             @PathVariable int status,
                                                             @PathVariable String token) {
        Map<String, String> response = new HashMap<>();
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation != null) return validation;

        appointmentService.changeStatus(appointmentId, status);
        response.put("message", "Status updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/patient/{token}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation != null) return validation;

        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return patientService.getPatientAppointment(patient.getId());
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error fetching appointments");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
