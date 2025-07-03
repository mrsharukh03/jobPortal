
# JobPortal

A Spring Boot based job portal application for students, recruiters, and admins to manage profiles, job applications, and postings.

---

## Features

* Student profile management with education, skills, and personal details
* Resume and profile picture upload support
* User authentication and authorization (Signup, Login, Verify)
* Recruiter dashboard to post and manage jobs
* Admin dashboard for overseeing the platform
* Secure APIs for profile updates and job applications

---

## Technologies Used

* Java 21
* Spring Boot
* Spring Security
* ModelMapper
* JPA/Hibernate
* MySQL
* Maven
* RESTful APIs
* Multipart file upload (for resumes and images)

---

## Setup and Installation

1. Clone the repo:

   ```bash
   git clone https://github.com/yourusername/jobPortal.git
   cd jobPortal
   ```

2. Configure your database in `src/main/resources/application.yml`.

3. Build the project:

   ```bash
   ./mvnw clean install
   ```

4. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

5. API base URL: `http://localhost:8080/api/v1/`

---

## API Endpoints Overview

### Student APIs

* `POST /student/upload` – Upload profile picture & resume
* `POST /student/update/skills` – Update student skills
* `POST /student/update/personalInfo` – Update personal details
* `POST /student/update/education` – Update education info
* `GET /student/profile` – Get student profile
* `GET /student/applications` – Get student's job applications

### User APIs

* `POST /auth/signup` – Register new user
* `POST /auth/login` – Login user
* `POST /auth/verify/{email}` – Verify email
* `GET /auth/test` – Test endpoint

### Recruiter APIs

* `GET /recruiter/dashboard` – Recruiter dashboard

### Admin APIs

* `GET /admin/dashboard` – Admin dashboard

---

## Contributing

Feel free to fork the repo and create pull requests. Issues and suggestions are welcome!

---

## License

MIT License (or your chosen license)
