# Smart Clinic Management System – Architecture Design

## Architecture Summary

The Smart Clinic Management System is a Spring Boot application built using a three-tier architecture. The presentation layer includes Thymeleaf-based dashboards for admin and doctor users, as well as REST API clients for patients and appointment management. The application layer consists of Spring MVC and REST controllers that handle requests and delegate business logic to service classes. The data layer uses two databases: MySQL for structured relational data such as patients, doctors, appointments, and admins, and MongoDB for document-based prescription records.

All incoming requests flow through controllers into a shared service layer, which applies validation and business rules before interacting with repositories. MySQL access is handled using Spring Data JPA with entity models, while MongoDB access is handled using Spring Data MongoDB with document models. This design ensures scalability, maintainability, and separation of concerns.

## Numbered Flow of Data and Control

1. A user accesses the system through a web browser or API client.
2. Admin and Doctor users interact with Thymeleaf-rendered dashboards, while patients use REST APIs.
3. Requests are routed to either MVC controllers or REST controllers based on the endpoint.
4. Controllers delegate processing to service classes that contain business logic.
5. Services interact with repository interfaces to fetch or store data.
6. MySQL repositories manage structured data, while MongoDB repositories manage prescription documents.
7. Retrieved data is mapped to models or DTOs and returned as HTML views or JSON responses.
