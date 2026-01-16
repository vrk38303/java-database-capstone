# Smart Clinic Management System – Database Schema Design

## MySQL Database Design

This document describes the MySQL database schema for the Smart Clinic Management System. The schema includes four well-defined tables with appropriate field names, data types, and foreign key relationships.

---

## Entity Relationship Diagram

```
+----------------+       +----------------+       +----------------+
|    doctors     |       |  appointments  |       |    patients    |
+----------------+       +----------------+       +----------------+
| id (PK)        |<------|doctor_id (FK)  |       | id (PK)        |
| name           |       | patient_id(FK) |------>| name           |
| specialty      |       | id (PK)        |       | email (UNIQUE) |
| email (UNIQUE) |       | appointment_time|      | password       |
| password       |       | status         |       | phone (UNIQUE) |
| phone          |       | condition      |       | address        |
+----------------+       +----------------+       +----------------+
        |
        v
+------------------------+
| doctor_available_times |
+------------------------+
| doctor_id (FK)         |
| time_slot              |
+------------------------+

+----------------+       +----------------+
|    admins      |       | prescriptions  |
+----------------+       +----------------+
| id (PK)        |       | id (PK)        |
| username(UNIQ) |       | appointment_id |
| password       |       | medication     |
+----------------+       | dosage         |
                         | patient_name   |
                         | doctor_notes   |
                         +----------------+
```

---

## Table Definitions

### 1. doctors
Stores information about doctors registered in the system.

| Column     | Data Type    | Constraints          | Description                    |
|------------|--------------|----------------------|--------------------------------|
| id         | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| name       | VARCHAR(100) | NOT NULL             | Doctor's full name             |
| specialty  | VARCHAR(50)  | NOT NULL             | Medical specialty              |
| email      | VARCHAR(255) | NOT NULL, UNIQUE     | Login email address            |
| password   | VARCHAR(255) | NOT NULL             | Hashed password                |
| phone      | VARCHAR(20)  | NOT NULL             | Contact phone number           |

```sql
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialty VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL
);
```

### 2. patients
Stores patient registration and login information.

| Column   | Data Type    | Constraints          | Description               |
|----------|--------------|----------------------|---------------------------|
| id       | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| name     | VARCHAR(100) | NOT NULL             | Patient's full name       |
| email    | VARCHAR(255) | NOT NULL, UNIQUE     | Login email address       |
| password | VARCHAR(255) | NOT NULL             | Hashed password           |
| phone    | VARCHAR(20)  | NOT NULL, UNIQUE     | Contact phone number      |
| address  | VARCHAR(255) |                      | Patient's address         |

```sql
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    address VARCHAR(255)
);
```

### 3. appointments
Stores appointment bookings between doctors and patients.

| Column           | Data Type  | Constraints                      | Description                   |
|------------------|------------|----------------------------------|-------------------------------|
| id               | BIGINT     | PRIMARY KEY, AUTO_INCREMENT      | Unique identifier             |
| doctor_id        | BIGINT     | NOT NULL, FOREIGN KEY (doctors)  | Reference to doctor           |
| patient_id       | BIGINT     | NOT NULL, FOREIGN KEY (patients) | Reference to patient          |
| appointment_time | DATETIME   | NOT NULL                         | Date and time of appointment  |
| status           | INT        | NOT NULL                         | Status code (0=pending, 1=completed) |
| condition        | VARCHAR(255)|                                 | Patient's condition/symptoms  |

```sql
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_time DATETIME NOT NULL,
    status INT NOT NULL DEFAULT 0,
    condition VARCHAR(255),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);
```

### 4. admins
Stores admin login credentials for system management.

| Column   | Data Type    | Constraints          | Description           |
|----------|--------------|----------------------|-----------------------|
| id       | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| username | VARCHAR(255) | NOT NULL, UNIQUE     | Admin username        |
| password | VARCHAR(255) | NOT NULL             | Hashed password       |

```sql
CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
```

### 5. doctor_available_times
Junction table for doctor availability slots.

| Column    | Data Type    | Constraints                     | Description            |
|-----------|--------------|--------------------------------|------------------------|
| doctor_id | BIGINT       | NOT NULL, FOREIGN KEY (doctors) | Reference to doctor   |
| time_slot | VARCHAR(10)  |                                | Available time (HH:mm) |

```sql
CREATE TABLE doctor_available_times (
    doctor_id BIGINT NOT NULL,
    time_slot VARCHAR(10),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);
```

### 6. prescriptions
Stores prescription records for completed appointments.

| Column         | Data Type    | Constraints          | Description                    |
|----------------|--------------|----------------------|--------------------------------|
| id             | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Unique identifier       |
| appointment_id | BIGINT       | NOT NULL             | Reference to appointment       |
| medication     | VARCHAR(100) | NOT NULL             | Prescribed medication name     |
| dosage         | VARCHAR(255) | NOT NULL             | Dosage instructions            |
| patient_name   | VARCHAR(100) | NOT NULL             | Patient's name                 |
| doctor_notes   | VARCHAR(200) |                      | Additional notes from doctor   |

```sql
CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    medication VARCHAR(100) NOT NULL,
    dosage VARCHAR(255) NOT NULL,
    patient_name VARCHAR(100) NOT NULL,
    doctor_notes VARCHAR(200)
);
```

---

## Foreign Key Relationships

1. **appointments.doctor_id** → **doctors.id**: Each appointment is assigned to one doctor
2. **appointments.patient_id** → **patients.id**: Each appointment is booked by one patient
3. **doctor_available_times.doctor_id** → **doctors.id**: Each time slot belongs to one doctor

---

## Architecture Summary

The Smart Clinic Management System uses a PostgreSQL database (adapted from MySQL for cloud deployment). The schema follows relational database principles with proper normalization, ensuring data integrity through foreign key constraints. Spring Data JPA is used to map these tables to Java entity classes with appropriate annotations.
