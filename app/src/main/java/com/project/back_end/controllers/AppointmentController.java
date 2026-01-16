package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
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
}
