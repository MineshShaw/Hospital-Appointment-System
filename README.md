# Hospital Appointment System

Spring Boot REST API for managing hospital appointments with JWT authentication, role-based access control, doctor availability, specialization management, and patient/doctor profiles.

**Highlights**
- Java 21 + Spring Boot 3.2
- JWT auth with stateless security
- Roles: ADMIN, DOCTOR, PATIENT
- MySQL persistence via Spring Data JPA
- Validation, Swagger UI (springdoc) at `/swagger-ui`
- Seeded admin user for first-run bootstrapping

**Tech Stack**
- Spring Boot Web, Security, Data JPA, Validation
- MySQL (`mysql-connector-j`)
- JSON Web Tokens (jjwt)
- Springdoc OpenAPI 2.x
- Mail (SMTP)

---

## Overview
This service enables:
- Patient registration and login, self-managed profile
- Doctor registration (requires admin approval) and schedule management
- Admin approval/rejection of new doctors
- Doctor availability slots (create/list)
- Appointment booking/cancellation and listings for both patients and doctors
- Specialization CRUD (admin-only)
 
---

## Project Structure

``` bash
main
├───java
│   └───com
│       └───example
│           └───hospitalAppointmentSystem
│               │   HospitalAppointmentSystemApplication.java
│               │
│               ├───config
│               │       AdminInitializer.java
│               │       JwtAuthenticationFilter.java
│               │       SecurityConfig.java
│               │
│               ├───controller
│               │       AdminController.java
│               │       AppointmentController.java
│               │       AuthController.java
│               │       AvailabilityController.java
│               │       DoctorController.java
│               │       PatientController.java
│               │       SpecializationController.java
│               │
│               ├───dto
│               │   ├───appointment
│               │   │       AppointmentCreateRequestDTO.java
│               │   │       AppointmentResponseDTO.java
│               │   │
│               │   ├───auth
│               │   │       AuthResponseDTO.java
│               │   │       LoginRequestDTO.java
│               │   │       RegisterRequestDTO.java
│               │   │       UserType.java
│               │   │
│               │   ├───availability
│               │   │       AvailabilityCreateRequestDTO.java
│               │   │       AvailabilityResponseDTO.java
│               │   │
│               │   ├───doctor
│               │   │       DoctorProfileResponseDTO.java
│               │   │       DoctorProfileUpdateDTO.java
│               │   │
│               │   ├───patient
│               │   │       PatientProfileResponseDTO.java
│               │   │       PatientProfileUpdateDTO.java
│               │   │
│               │   └───specialization
│               │           SpecializationCreateRequestDTO.java
│               │           SpecializationResponseDTO.java
│               │           SpecializationUpdateRequestDTO.java
│               │
│               ├───exception
│               │       GlobalExceptionHandler.java
│               │
│               ├───model
│               │       Appointment.java
│               │       AppointmentStatus.java
│               │       AvailabilityStatus.java
│               │       DoctorAvailability.java
│               │       DoctorProfile.java
│               │       PatientProfile.java
│               │       Role.java
│               │       Specialization.java
│               │       User.java
│               │
│               ├───repository
│               │       AppointmentRepository.java
│               │       DoctorAvailabilityRepository.java
│               │       DoctorProfileRepository.java
│               │       PatientProfileRepository.java
│               │       SpecializationRepository.java
│               │       UserRepository.java
│               │
│               ├───service
│               │       AppointmentService.java
│               │       AuthService.java
│               │       AvailabilityService.java
│               │       CustomUserDetailsService.java
│               │       DoctorProfileService.java
│               │       EmailService.java
│               │       PatientProfileService.java
│               │       SpecializationService.java
│               │       UserService.java
│               │
│               └───util
│                       JwtUtil.java
│
└───resources
        application.yaml

```

---

## Security & Roles
Security is configured in [SecurityConfig.java](src/main/java/com/example/hospitalAppointmentSystem/config/SecurityConfig.java):
- Public endpoints: `/api/auth/**`, `/swagger-ui/**`, `/api-docs/**`
- Admin-only: `/api/specializations/**`
- All other endpoints require authentication; fine-grained access is enforced via `@PreAuthorize` in controllers.

JWT is processed by [JwtAuthenticationFilter.java](src/main/java/com/example/hospitalAppointmentSystem/config/JwtAuthenticationFilter.java).

Seed admin user is created at startup by [AdminInitializer.java](src/main/java/com/example/hospitalAppointmentSystem/config/AdminInitializer.java):
- Email: `admin@hospital.com`
- Password: `Admin123!`

---

## Configuration
Application settings are in [application.yaml](src/main/resources/application.yaml).

Required environment variables:
- **DB_PASSWORD**: MySQL password for user `root`
- **APP_EMAIL**: SMTP username (e.g., Gmail address)
- **APP_PASSWORD**: SMTP password or app password

JWT settings (recommend moving `jwt.secret` to an environment variable in production):
- `jwt.secret`: current static secret (replace for production)
- `jwt.expiration`: token TTL in ms (default 86,400,000 = 24h)

Database:
- URL: `jdbc:mysql://localhost:3306/hospital_appointment_system`
- Hibernate: `ddl-auto: update` (auto creates/updates schema)

Springdoc:
- API Docs: `/api-docs`
- Swagger UI: `/swagger-ui`

---

## Getting Started

Prerequisites:
- Java 21 (see [pom.xml](pom.xml))
- MySQL running locally, database `hospital_appointment_system`

Install and run (Windows):

```bash
# From project root
set DB_PASSWORD=your_mysql_password
set APP_EMAIL=your_email@example.com
set APP_PASSWORD=your_email_app_password

# Run the app
./mvnw.cmd spring-boot:run

# Or build a jar
./mvnw.cmd clean package
java -jar target/Hospital-Appointment-System-2.3.0-SNAPSHOT.jar
```

Swagger UI:
- Browse to `http://localhost:8080/swagger-ui`
- Use the `Authorize` button with `Bearer <your_jwt_token>`

---

## Authentication
Endpoints are in [AuthController.java](src/main/java/com/example/hospitalAppointmentSystem/controller/AuthController.java).

- POST `/api/auth/register`
	- Registers a new `PATIENT` or `DOCTOR`
	- Request example:
		```json
		{
			"email": "john@example.com",
			"password": "StrongP@ssw0rd",
			"userType": "PATIENT",
			"fullName": "John Doe"
		}
		```
	- Doctors start inactive; ADMIN must approve.

- POST `/api/auth/login`
	- Returns JWT token
	- Request example:
		```json
		{ "email": "john@example.com", "password": "StrongP@ssw0rd" }
		```
	- Response example:
		```json
		{ "token": "<jwt>" }
		```

Include `Authorization: Bearer <token>` header for protected endpoints.

---

## Admin – Doctor Management
Endpoints are in [AdminController.java](src/main/java/com/example/hospitalAppointmentSystem/controller/AdminController.java) and require `ADMIN`.

- PATCH `/api/admin/doctors/{id}/approve`
- DELETE `/api/admin/doctors/{id}/reject`

---

## Doctor Endpoints
Defined in [DoctorController.java](src/main/java/com/example/hospitalAppointmentSystem/controller/DoctorController.java).

- GET `/api/doctors/me` — get own profile
- PUT `/api/doctors/me` — update own profile
- GET `/api/doctors/{id}` — get a doctor profile by user ID
- GET `/api/doctors/search` — search by `name` or `specializationId`
	- Note: This method is annotated to allow `PATIENT`, but class-level `@PreAuthorize("hasRole('DOCTOR')")` may restrict access. Adjust as needed if public search is desired.

---

## Patient Endpoints
Defined in [PatientController.java](src/main/java/com/example/hospitalAppointmentSystem/controller/PatientController.java). Require `PATIENT`.

- GET `/api/patients/me` — get own profile
- PUT `/api/patients/me` — update own profile

---

## Availability (Doctor)
Defined in [AvailabilityController.java](src/main/java/com/example/hospitalAppointmentSystem/controller/AvailabilityController.java).

- POST `/api/availabilities` — create a slot (DOCTOR)
	- Request example:
		```json
		{
			"startTime": "2026-01-25T10:00:00",
			"endTime": "2026-01-25T10:30:00"
		}
		```
- GET `/api/availabilities/doctor/{doctorId}` — list available slots
	- Note: Controller description says "accessible to all authenticated users", but class-level `@PreAuthorize("hasRole('DOCTOR')")` applies; update annotations if broader access is needed.

---

## Appointments
Defined in [AppointmentController.java](src/main/java/com/example/hospitalAppointmentSystem/controller/AppointmentController.java).

- POST `/api/appointments` (PATIENT) — book using `availabilityId`
	- Request example:
		```json
		{
			"availabilityId": 123,
			"reason": "Routine checkup",
			"symptoms": "N/A",
			"notes": "Prefer morning"
		}
		```
- POST `/api/appointments/{id}/cancel` (PATIENT) — cancel own appointment
- GET `/api/appointments/patient/me` (PATIENT) — list my appointments
- GET `/api/appointments/doctor/me` (DOCTOR) — list my appointments

---

## Error Handling
Global exception handling is in [GlobalExceptionHandler.java](src/main/java/com/example/hospitalAppointmentSystem/exception/GlobalExceptionHandler.java). Server is configured to include stack traces in error responses for development:
- `server.error.include-stacktrace: ALWAYS`
- `server.error.include-message: ALWAYS`

Consider reducing verbosity for production.

---

## Development Notes
- Code style: DTOs live under `dto/*`, services under `service/*`, entities under `model/*`.
- Doctor approval requires the doctor to have at least one specialization.
- Prefer moving secrets (JWT, mail) to environment variables.
- Enable SQL logging by setting `spring.jpa.show-sql: true` for troubleshooting.

---

## Troubleshooting
- 401 Unauthorized: Ensure `Authorization: Bearer <token>` header is present.
- 403 Forbidden: Check your role and endpoint restrictions.
- DB connection errors: Verify MySQL is running and `DB_PASSWORD` is set.
- Swagger not loading: Confirm `springdoc` paths `/api-docs` and `/swagger-ui` are reachable.

---
