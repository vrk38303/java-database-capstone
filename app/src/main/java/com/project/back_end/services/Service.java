package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public Service(TokenService tokenService, AdminRepository adminRepository, DoctorRepository doctorRepository,
                   PatientRepository patientRepository, PatientService patientService, DoctorService doctorService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String role) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, role)) {
            response.put("error", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    public ResponseEntity<Map<String, Object>> validateAdmin(Login login) {
        Map<String, Object> response = new HashMap<>();
        try {
            var admin = adminRepository.findByUsername(login.getEmail());
            if (admin == null) {
                response.put("error", "Invalid username or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            if (!admin.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid username or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            response.put("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Doctor> filterDoctor(String name, String specialty, String time) {
        boolean hasName = name != null && !name.isEmpty() && !name.equals("null");
        boolean hasSpecialty = specialty != null && !specialty.isEmpty() && !specialty.equals("null");
        boolean hasTime = time != null && !time.isEmpty() && !time.equals("null");

        if (hasName && hasSpecialty && hasTime) {
            return doctorService.filterDoctorsByNameSpecialityAndTime(name, specialty, time);
        } else if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecialty(name, specialty);
        } else if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        } else if (hasSpecialty && hasTime) {
            return doctorService.filterDoctorByTimeAndSpecialty(time, specialty);
        } else if (hasName) {
            return doctorService.findDoctorByName(name);
        } else if (hasSpecialty) {
            return doctorService.filterDoctorBySpecialty(specialty);
        } else if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        } else {
            return doctorService.getDoctors();
        }
    }

    public int validateAppointment(Long doctorId, LocalTime time) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return -1;
        }
        Doctor doctor = doctorOpt.get();
        List<String> availableTimes = doctor.getAvailableTimes();
        if (availableTimes == null) {
            return 0;
        }
        String timeStr = time.toString();
        for (String slot : availableTimes) {
            if (slot.equals(timeStr)) {
                return 1;
            }
        }
        return 0;
    }

    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    public ResponseEntity<Map<String, Object>> validatePatientLogin(Login login) {
        Map<String, Object> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null) {
                response.put("error", "Invalid email or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("error", "Invalid email or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("patientId", patient.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("error", "Internal server error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String token, String condition, String doctorName) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Patient not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            boolean hasCondition = condition != null && !condition.isEmpty() && !condition.equals("null");
            boolean hasDoctor = doctorName != null && !doctorName.isEmpty() && !doctorName.equals("null");

            if (hasCondition && hasDoctor) {
                return patientService.filterByDoctorAndCondition(patient.getId(), doctorName, condition);
            } else if (hasCondition) {
                return patientService.filterByCondition(patient.getId(), condition);
            } else if (hasDoctor) {
                return patientService.filterByDoctor(patient.getId(), doctorName);
            } else {
                return patientService.getPatientAppointment(patient.getId());
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error filtering appointments: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
