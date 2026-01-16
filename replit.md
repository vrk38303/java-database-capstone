# Smart Clinic Management System

## Overview
A full-stack Spring Boot clinic management application with role-based access for Admin, Doctor, and Patient users. Features appointment booking, prescription management, and comprehensive user dashboards.

## Architecture
- **Backend**: Spring Boot 3.4.4 with Java 17
- **Database**: PostgreSQL (Replit-managed)
- **Frontend**: Thymeleaf templates + Static HTML/CSS/JS
- **Authentication**: JWT tokens

## Project Structure
```
app/
├── src/main/java/com/project/back_end/
│   ├── models/          # JPA entities (Doctor, Patient, Appointment, etc.)
│   ├── repo/            # Spring Data JPA repositories
│   ├── services/        # Business logic layer
│   ├── controllers/     # REST API endpoints
│   ├── mvc/             # Thymeleaf dashboard controllers
│   ├── config/          # Web configuration
│   └── DTO/             # Data transfer objects
├── src/main/resources/
│   ├── static/          # Frontend assets (CSS, JS, HTML pages)
│   ├── templates/       # Thymeleaf templates (admin/doctor dashboards)
│   └── application.properties
├── Dockerfile           # Multi-stage Docker build
└── .github/workflows/   # GitHub Actions CI/CD
```

## Key Files for Grading

| Question | File Location | What to Look For |
|----------|--------------|------------------|
| Q1 | `user_stories.md` | User stories for Admin, Doctor, Patient |
| Q2 | `schema-design.md` | MySQL/PostgreSQL table definitions |
| Q3 | `app/src/main/java/.../models/Doctor.java` | JPA entity, @Id, @ElementCollection for availableTimes |
| Q4 | `app/src/main/java/.../models/Appointment.java` | @ManyToOne relationships, @FutureOrPresent validation |
| Q5 | `app/src/main/java/.../controllers/DoctorController.java` | GET /availability endpoint with token validation |
| Q6 | `app/src/main/java/.../services/AppointmentService.java` | bookAppointment(), getAppointments() methods |
| Q7 | `app/src/main/java/.../controllers/PrescriptionController.java` | POST with @Valid, ResponseEntity |
| Q8 | `app/src/main/java/.../repo/PatientRepository.java` | findByEmail(), findByEmailOrPhone() |
| Q9 | `app/src/main/java/.../services/TokenService.java` | generateToken(), getSigningKey() |
| Q10 | `app/src/main/java/.../services/DoctorService.java` | getDoctorAvailability(), validateDoctor() |
| Q11 | `app/Dockerfile` | Multi-stage build, EXPOSE 8080, ENTRYPOINT |
| Q12 | `app/.github/workflows/maven-build.yml` | Java/Maven CI workflow |

## Test Credentials
- **Admin**: username=admin, password=admin123
- **Doctor**: email=john.smith@clinic.com, password=doctor123
- **Patient**: email=alice@email.com, password=patient123

## API Endpoints
- `GET /doctor` - List all doctors
- `POST /doctor/login` - Doctor login
- `POST /patient/login` - Patient login
- `POST /admin/login` - Admin login
- `GET /doctor/filter/{name}/{time}/{specialty}` - Filter doctors
- `GET /appointment/{doctorId}/{date}/{patientName}/{token}` - Get appointments

## Running Locally
```bash
cd app && ./mvnw spring-boot:run
```

## Stored Procedures (PostgreSQL)
- `GetDailyAppointmentReportByDoctor(doctor_id, date)` - Daily appointment report
- `GetDoctorWithMostPatientsByMonth(year, month)` - Monthly top doctor
- `GetDoctorWithMostPatientsByYear(year)` - Yearly top doctor

## Recent Changes
- 2026-01-16: Implemented complete frontend dashboards
- 2026-01-16: Added stored procedures for reporting
- 2026-01-16: Converted from MySQL to PostgreSQL
- 2026-01-16: Created GitHub Actions workflow
