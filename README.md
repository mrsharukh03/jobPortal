
<div align="center">

# 💼 JobPortal

**A production-ready, full-stack Job Portal REST API built with Spring Boot**

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/Database-MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apachemaven)](https://maven.apache.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)

A scalable, secure, and feature-rich Job Portal backend that connects **Job Seekers**, **Recruiters**, and **Admins** on a unified platform — with JWT authentication, role-based access control, AI-powered resume parsing, and real-time email notifications.

[Features](#-features) · [Tech Stack](#-tech-stack) · [Getting Started](#-getting-started) · [API Reference](#-api-reference) · [Architecture](#-architecture) · [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Configuration](#configuration)
- [API Reference](#-api-reference)
  - [Authentication](#authentication-apiv1auth)
  - [Public Endpoints](#public-endpoints-apiv1public)
  - [Job Seeker](#job-seeker-apiv1seeker)
  - [Recruiter](#recruiter-apiv1recruiter)
  - [Job Management](#job-management-apiv1job)
  - [Admin](#admin-apiv1admin)
- [Security Model](#-security-model)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### 👤 Authentication & Authorization
- JWT-based stateless authentication (Access + Refresh tokens via HTTP-only cookies)
- Email verification on signup with token-based flow
- Password reset via secure email link
- Magic link / direct login support
- Role-based access control: `SEEKER`, `RECRUITER`, `ADMIN`, `SUPER_ADMIN`

### 🔍 Job Seeker
- Build a rich professional profile (personal details, education, experience, certifications, skills)
- Upload resume (PDF) and profile picture (multipart)
- Apply to jobs with one click
- Save / unsave jobs for later review
- View complete application history with status tracking
- AI-powered resume parsing *(in active development)*

### 🏢 Recruiter
- Post, update, and delete job listings
- Review and manage job applications
- Bulk-update application statuses (shortlist, reject, accept)
- Access a real-time dashboard with hiring analytics and overview metrics

### 🔎 Job Discovery
- Browse popular jobs with pagination
- Filter jobs by category
- Advanced search with multiple filters (keyword, location, type, salary range, etc.)

### 🛡️ Admin
- Platform-wide oversight dashboard
- Activate / verify user accounts
- Dedicated admin authentication flow

### 📧 Notifications
- Transactional email via Spring Mail (SMTP)
- Verification emails, password reset links, application status updates

### 📖 API Documentation
- Interactive Swagger UI: [`http://localhost:8080/swagger-ui/index.html`](http://localhost:8080/swagger-ui/index.html)
- OpenAPI 3.0 spec auto-generated via SpringDoc

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 21 |
| Framework | Spring Boot | 3.5.3 |
| Security | Spring Security + JWT (jjwt) | 3.5.3 / 0.12.6 |
| Persistence | Spring Data JPA + Hibernate | 3.5.3 |
| Database | MySQL | 8.x |
| API Docs | SpringDoc OpenAPI (Swagger UI) | 2.8.3 |
| Mapping | ModelMapper | 3.2.4 |
| Boilerplate | Lombok | 1.18.36 |
| Build Tool | Apache Maven | 3.x |
| Runtime | Spring Boot DevTools | 3.5.3 |
| Email | Spring Boot Mail (SMTP) | 3.5.3 |
| Reactive | Spring WebFlux (WebClient) | 3.5.3 |

---

## 🏗 Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                         Client (Frontend)                        │
│                  React / Vue / Mobile / Postman                  │
└────────────────────────────┬─────────────────────────────────────┘
                             │ HTTP / REST
┌────────────────────────────▼─────────────────────────────────────┐
│                     Spring Boot Application                      │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │            Spring Security (JWT Filter Chain)               │ │
│  └───────────────────────────┬─────────────────────────────────┘ │
│                              │                                   │
│  ┌───────────────────────────▼─────────────────────────────────┐ │
│  │                    Controller Layer                          │ │
│  │  Auth │ Seeker │ Recruiter │ Job │ Public │ Admin │ AI      │ │
│  └───────────────────────────┬─────────────────────────────────┘ │
│                              │                                   │
│  ┌───────────────────────────▼─────────────────────────────────┐ │
│  │                     Service Layer                            │ │
│  │  UserService │ JobService │ SeekerService │ RecruiterService │ │
│  │  MailService │ AIService  │ AdminService  │ SavedJobService  │ │
│  └───────────────────────────┬─────────────────────────────────┘ │
│                              │                                   │
│  ┌───────────────────────────▼─────────────────────────────────┐ │
│  │                   Repository Layer (JPA)                     │ │
│  └───────────────────────────┬─────────────────────────────────┘ │
│                              │                                   │
└──────────────────────────────┼───────────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────────┐
│                          MySQL Database                          │
└──────────────────────────────────────────────────────────────────┘
```

### Domain Model

```
User (base)
├── Seeker
│   ├── Education[]
│   ├── Experience[]
│   ├── Certification[]
│   ├── Skill[]
│   ├── JobApplication[]
│   └── SavedJob[]
├── Recruiter
│   └── JobPost[]
│       └── JobApplication[]
└── Admin
```

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:

| Tool | Version |
|---|---|
| JDK | 21+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Git | 2.x+ |

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/mrsharukh03/jobPortal.git
cd jobPortal
```

**2. Create the database**
```sql
CREATE DATABASE job_portal;
```

**3. Configure the application**

Copy or edit `src/main/resources/application.yml` with your environment values (see [Configuration](#configuration) below).

**4. Build the project**
```bash
./mvnw clean install -DskipTests
```

**5. Run the application**
```bash
./mvnw spring-boot:run
```

The server starts at: **`http://localhost:8080`**  
Swagger UI available at: **`http://localhost:8080/swagger-ui/index.html`**

---

### Configuration

Set the following environment variables (or update `application.yml` directly):

```yaml
# application.yml — key configuration properties

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/job_portal
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}       # Gmail App Password

app:
  jwt:
    secret: ${JWT_SECRET}            # Min 256-bit base64-encoded key
    access-token-expiry: 900000      # 15 minutes (ms)
    refresh-token-expiry: 604800000  # 7 days (ms)

  ai:
    provider: ${AI_PROVIDER}
    api-key: ${AI_API_KEY}
    model: ${AI_MODEL}

  cors:
    allowed-origins: http://localhost:5173
```

| Variable | Description |
|---|---|
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `MAIL_USERNAME` | SMTP email address |
| `MAIL_PASSWORD` | SMTP App password |
| `JWT_SECRET` | HS256 secret key (base64) |
| `AI_API_KEY` | AI provider API key (resume parsing) |

---

## 📡 API Reference

All endpoints are prefixed with `/api/v1`.  
**Protected routes** require a valid JWT access token (sent automatically via HTTP-only cookie after login).

---

### Authentication (`/api/v1/auth`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/signup` | Public | Register a new user |
| `POST` | `/login` | Public | Login and receive JWT cookies |
| `POST` | `/refresh` | Public | Refresh access token |
| `POST` | `/logout` | Authenticated | Clear auth cookies |
| `POST` | `/email/verify` | Public | Verify email address |
| `POST` | `/password/forget` | Public | Send password reset email |
| `POST` | `/password/reset` | Public | Reset password via token |
| `POST` | `/direct-login` | Public | Magic link login |

---

### Public Endpoints (`/api/v1/public`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/popular-jobs` | Public | Paginated list of popular jobs |
| `GET` | `/jobs/{category}` | Public | Jobs filtered by category |
| `GET` | `/job/{id}` | Public | Single job details |
| `POST` | `/search` | Public | Advanced job search with filters |

---

### Job Seeker (`/api/v1/seeker`)

> Requires role: `SEEKER`

**Profile**

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/current-profile` | Full seeker profile |
| `GET` | `/personal-details` | Personal information |
| `PATCH` | `/update/personal-details` | Update personal info |
| `GET` | `/professional` | Professional summary |
| `PATCH` | `/update-professional` | Update professional details |
| `POST` | `/upload-documents` | Upload resume & profile picture |

**Education**

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/education` | List education entries |
| `POST` | `/education` | Add new education entry |
| `PUT` | `/education/{id}` | Update education entry |
| `DELETE` | `/education/{id}` | Delete education entry |

**Experience**

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/experience` | List work experience |
| `POST` | `/experience` | Add experience |
| `PUT` | `/experience/{id}` | Update experience |
| `DELETE` | `/experience/{id}` | Delete experience |

**Certifications**

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/certifications` | List certifications |
| `POST` | `/certifications` | Add certification |
| `DELETE` | `/certifications/{id}` | Delete certification |

**Skills**

| Method | Endpoint | Description |
|---|---|---|
| `DELETE` | `/skill/{skillId}` | Remove a skill |

**Jobs & Applications**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/job/{jobId}/apply` | Apply to a job |
| `GET` | `/applications` | All applications |
| `GET` | `/application/{id}` | Application details |

**Saved Jobs**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/saved-jobs/save` | Save a job |
| `GET` | `/api/v1/saved-jobs/saved` | List saved jobs |
| `DELETE` | `/api/v1/saved-jobs/unsave` | Remove saved job |

---

### Recruiter (`/api/v1/recruiter`)

> Requires role: `RECRUITER`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/profile` | Get recruiter profile |
| `POST` | `/profile/update` | Update recruiter profile |
| `GET` | `/dashboard/overview` | Dashboard analytics & metrics |
| `PUT` | `/applications/{id}/status` | Update single application status |
| `PUT` | `/applications/status/bulk` | Bulk update application statuses |

---

### Job Management (`/api/v1/job`)

> Requires role: `RECRUITER`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Post a new job |
| `PUT` | `/{postId}` | Update job posting |
| `DELETE` | `/{postId}` | Delete job posting |
| `GET` | `/recruiter/` | Recruiter's posted jobs (paginated) |
| `GET` | `/{jobId}/applications` | Applications for a job |
| `GET` | `/applications/{id}` | Specific application details |

---

### Admin (`/api/v1/admin`)

> Requires role: `ADMIN` or `SUPER_ADMIN`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/dashboard` | Admin dashboard overview |
| `POST` | `/user/activate` | Activate / verify a user account |

---

### Application Status Values

| Status | Description |
|---|---|
| `APPLIED` | Initial state after applying |
| `SHORTLISTED` | Recruiter shortlisted the candidate |
| `ACCEPTED` | Candidate accepted for the role |
| `REJECTED` | Application rejected |

---

## 🔐 Security Model

### Authentication Flow

```
1. POST /api/v1/auth/signup  →  User registered (email verification sent)
2. POST /api/v1/auth/email/verify  →  Account activated
3. POST /api/v1/auth/login  →  Access token + Refresh token set as HTTP-only cookies
4. Subsequent requests  →  JWT filter validates access token from cookie
5. POST /api/v1/auth/refresh  →  New access token issued using refresh token
6. POST /api/v1/auth/logout  →  Cookies cleared
```

### Token Details

| Token | Lifetime | Storage |
|---|---|---|
| Access Token | 15 minutes | HTTP-only cookie |
| Refresh Token | 7 days | HTTP-only cookie |

### Role Hierarchy

```
SUPER_ADMIN  →  Full platform control
ADMIN        →  User management, platform oversight
RECRUITER    →  Job posting, application management
SEEKER       →  Job discovery, applications, profile
```

---

## 📁 Project Structure

```
src/
└── main/
    ├── java/com/jobPortal/
    │   ├── Controller/          # REST API controllers
    │   ├── Service/             # Business logic layer
    │   ├── Repository/          # Spring Data JPA repositories
    │   ├── Model/               # JPA entity classes
    │   │   ├── User.java        # Base user entity (inheritance root)
    │   │   ├── Seeker.java
    │   │   ├── Recruiter.java
    │   │   ├── Admin.java
    │   │   ├── JobPost.java
    │   │   ├── JobApplication.java
    │   │   └── ...
    │   ├── DTO/                 # Data Transfer Objects (request/response)
    │   │   ├── AuthDTO/
    │   │   ├── JobSeekerDTO/
    │   │   └── RecruiterDTO/
    │   ├── Security/            # JWT filter, user details, principal
    │   ├── Config/              # Spring beans, security config, CORS
    │   ├── Exception/           # Global exception handler, custom exceptions
    │   ├── Enums/               # Role, JobType, SkillLevel, ApplicationStatus
    │   ├── AIEngine/            # AI resume parsing integration
    │   ├── Mapper/              # ModelMapper configurations
    │   └── Util/                # Helper utilities, validators, specifications
    └── resources/
        └── application.yml      # Application configuration
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** your feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Commit** your changes with a clear message:
   ```bash
   git commit -m "feat: add your feature description"
   ```
4. **Push** to your branch:
   ```bash
   git push origin feature/your-feature-name
   ```
5. **Open a Pull Request** against the `main` branch

### Code Style
- Follow standard Java naming conventions
- Keep controllers thin — business logic belongs in the service layer
- All new endpoints must be documented with Swagger/OpenAPI annotations
- Validate all incoming request bodies using `jakarta.validation` annotations

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ using Spring Boot

</div>
