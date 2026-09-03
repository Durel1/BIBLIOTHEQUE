# 📚 biblio

A full-stack web application for managing a **private personal book collection**.

This project is a complete modernization of an initial PHP/MySQL application, rebuilt around a REST architecture using **Spring Boot, PostgreSQL and Vanilla JavaScript**.

---

## 🌐 Live Application

### Frontend
👉 https://bibliotheque-durel.vercel.app

### Backend API
👉 https://bibliotheque-api-4oxh.onrender.com

> The backend API contains protected endpoints, so accessing some URLs directly without authentication may return `401 Unauthorized`.

---

## ✨ Features

- User registration
- Secure password hashing with BCrypt
- JWT-based authentication
- Private book collections
- Create, read, update and delete books
- Per-user book ownership
- Book cover support through external URLs
- Responsive web interface
- Centralized API error handling
- PostgreSQL schema versioning with Flyway
- Production deployment with Docker, Render, Vercel and Supabase

---

## 🏗️ Architecture

```text
User
 │
 │ HTTPS
 ▼
Vercel
HTML / CSS / Vanilla JavaScript
 │
 │ REST / JSON
 │ Authorization: Bearer JWT
 ▼
Render
Spring Boot REST API
 │
 ├── Spring Security
 ├── JWT Authentication
 ├── Controllers
 ├── Services
 ├── Spring Data JPA / Hibernate
 └── Flyway
 │
 │ JDBC / SSL
 ▼
Supabase
PostgreSQL
```

---

## ⚙️ Backend Architecture

The backend follows a layered architecture:

```text
HTTP Request
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
PostgreSQL
```

Responsibilities are separated between:

- **Controllers** — HTTP requests and responses
- **Services** — application and business logic
- **Repositories** — database access
- **Entities** — persistence model
- **DTOs** — API request and response models
- **Security components** — JWT authentication
- **Exception handlers** — standardized HTTP errors

---

## 🔐 Authentication

Authentication is stateless.

After a successful login, the backend generates a signed JWT.

The frontend sends this token with protected requests:

```http
Authorization: Bearer <token>
```

A custom Spring Security filter validates the token and restores the authenticated user in Spring Security's `SecurityContext`.

Authentication flow:

```text
Email + Password
       ↓
POST /api/auth/login
       ↓
BCrypt verification
       ↓
JWT generation
       ↓
Frontend stores JWT
       ↓
Authorization: Bearer <JWT>
       ↓
JwtAuthenticationFilter
       ↓
SecurityContext
       ↓
Authenticated Controller
```

---

## 👤 Book Ownership

Every book belongs to exactly one user.

Ownership is enforced at several levels:

- The authenticated user is derived from the JWT
- The frontend never chooses the owner of a book
- Repository queries are scoped by `user_id`
- Cross-user read, update and delete operations return HTTP `404`
- `books.user_id` is a `NOT NULL` foreign key in PostgreSQL
- The JPA relationship is mandatory

Conceptually:

```text
JWT
 │
 ▼
Authenticated User ID
 │
 ▼
BookRepository
 │
 ▼
WHERE user_id = authenticated_user_id
```

This prevents one authenticated user from accessing another user's books.

---

## 🚨 API Error Handling

API errors use a common JSON representation:

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "Not Found",
  "message": "Book not found: 42",
  "path": "/api/books/42"
}
```

Main HTTP errors:

| Status | Meaning |
|---|---|
| `400` | Validation error |
| `401` | Authentication error |
| `404` | Missing or inaccessible resource |
| `409` | Registration conflict |

---

## 🛠️ Technology Stack

### Backend

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- JWT
- BCrypt
- Flyway
- Maven

### Frontend

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

### Database

- PostgreSQL
- Supabase

### Testing

- JUnit
- Mockito
- MockMvc
- H2 in PostgreSQL compatibility mode
- Repository tests
- Service tests
- Controller tests

### Deployment

- Docker
- Render — backend
- Vercel — frontend
- Supabase — PostgreSQL
- GitHub — source control

---

## 📁 Project Structure

```text
BIBLIOTHEQUE/
├── backend/
│   ├── src/main/java/
│   │   └── com/durel/bibliotheque/
│   │       ├── config/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       ├── security/
│   │       └── service/
│   │
│   ├── src/main/resources/
│   │   └── db/migration/
│   │
│   ├── Dockerfile
│   └── .env.example
│
├── frontend/
│   ├── css/
│   ├── js/
│   ├── index.html
│   ├── login.html
│   └── register.html
│
├── docs/
│
└── legacy/
    └── php/
```

---

## 💻 Running Locally

### 1. Backend

Go to the backend directory:

```bash
cd backend
```

Create the local environment file:

```bash
cp .env.example .env
```

Configure your Supabase/PostgreSQL credentials and JWT secret.

Load the environment variables:

```bash
set -a
source .env
set +a
```

Run Spring Boot:

```bash
./mvnw spring-boot:run
```

The API runs by default on:

```text
http://localhost:8080
```

---

### 2. Frontend

From the project root:

```bash
cd frontend
python3 -m http.server 5500
```

Then open:

```text
http://localhost:5500
```

---

## 🧪 Tests

Run the complete backend test suite:

```bash
cd backend
./mvnw clean test
```

---

## 🗄️ Database Migrations

Flyway owns the database schema.

Hibernate is configured with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

This means Hibernate validates that the Java model matches the database schema but does not modify the production schema automatically.

Database changes are applied through versioned Flyway migrations.

Example evolution:

```text
V1 → books table
V2 → users table
V3 → user/book relationship
V4 → mandatory book ownership
```

---

## 🔒 Security Notes

Sensitive values such as:

```text
SUPABASE_DB_PASSWORD
JWT_SECRET
```

are never stored in the frontend or committed to Git.

Production secrets are configured as environment variables on Render.

The current V1 stores the JWT in browser `localStorage`.

For an application with stronger security requirements, a future evolution could use:

- Secure HttpOnly cookies
- Refresh tokens
- Stronger XSS protection
- Automated security integration tests

---

## 🐳 Production Deployment

The production architecture is:

```text
Browser
   │
   ▼
Vercel
Frontend
   │
   │ HTTPS / JSON / JWT
   ▼
Render
Docker + Spring Boot
   │
   │ JDBC / SSL
   ▼
Supabase
PostgreSQL
```

The Spring Boot application is packaged inside a Docker image and deployed on Render.

The frontend is deployed independently on Vercel.

CORS is configured on the backend so that only the authorized frontend origin can communicate with the API from a browser.

---

## 🤖 Development Approach

This project was built both as a **software modernization project** and as a **learning exercise**.

AI-assisted development was used as a pair-programming and learning tool for:

- Technical explanations
- Debugging
- Code review
- Architecture discussions
- Documentation
- Analysis of logs and errors

The project was developed progressively, with each major functionality implemented, tested and validated before moving to the next step.

The goal was not simply to generate code, but to understand the technologies, architecture decisions and development workflow used throughout the application.

---

## 🚀 Roadmap

Possible future improvements:

- Search and filtering
- Pagination
- Direct book-cover upload with Supabase Storage
- Refresh-token strategy
- Secure HttpOnly cookie authentication
- Automated Spring Security integration tests
- CI/CD quality checks
- Improved accessibility
- Better loading states and notifications

---

## 👨‍💻 Author

Developed as a full-stack Java/Spring Boot learning and modernization project.

**Live application:**  
https://bibliotheque-durel.vercel.app