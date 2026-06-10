# SnapLink | Real-Time URL Shortener & Analytics

SnapLink is a high-performance, production-ready URL shortener designed to demonstrate enterprise-grade architecture, real-time data streaming, and modern cloud deployment practices. 

Instead of a traditional static approach, SnapLink leverages **WebSockets** to deliver instant click analytics to the user dashboard the exact millisecond a link is accessed.

---

## 🚀 Key Features

*   **Lightning-Fast URL Shortening:** Converts long URLs into secure, unique short codes using a Base62 encoding algorithm.
*   **Real-Time Analytics Dashboard:** Visual counter that updates via WebSockets instantly whenever a link is visited.
*   **Secure Authentication:** User registration and stateful security backed by Spring Security and JWT.
*   **Premium UX/UI:** Fluid transitions, micro-interactions, and visual feedback powered by Framer Motion.
*   **Enterprise Architecture:** Clean Architecture / DDD separation on the backend to enforce the Single Responsibility Principle.

---

## 🛠️ Tech Stack & Architecture

### Backend
*   **Java 21** & **Spring Boot 3.x**
*   **Spring Security & JWT** (Authentication & Authorization)
*   **Spring WebSocket (STOMP)** (Real-time event broadcasting)
*   **Spring Data JPA** & **PostgreSQL**
*   **JUnit 5 & Mockito** (Unit & Integration Testing)

### Frontend
*   **React** (Vite) & **TypeScript**
*   **Tailwind CSS** (Modern utility-first styling)
*   **Framer Motion** (Advanced UI animations)
*   **StompJS / SockJS** (WebSocket client integration)

### DevOps & Infrastructure
*   **Docker & Docker Compose** (Full containerization of App, Frontend, and DB)
*   **GitHub Actions** (CI/CD Pipeline automated for testing and linting)
*   **Infrastructure:** Multi-container production setup ready for cloud environments.

---

## 📐 System Architecture

Whenever a short link is requested, the system performs a non-blocking redirect while concurrently broadcasting an update event:

[User/Client] ---> (HTTP 302 Redirect) ---> [Spring Boot API]
|
(Async Event Trigger)
|
v
[Dashboard UI] <--- (WebSocket Message) <--- [STOMP Broker]

---

## ⚡ Running Locally with Docker

You can spin up the entire ecosystem (Frontend, Backend, and Database) with a single command. Ensure you have **Docker** and **Docker Compose** installed.

1. Clone the repository:
```bash
   git clone [https://github.com/jardelbrando/snaplink.git](https://github.com/jardelbrando/snaplink.git)
   cd snaplink
```

2. Run the environment:
```bash
docker-compose up --build
```
3. Access the applications:
  - Frontend: http://localhost:3000
  - Backend API: http://localhost:8080
  - API Documentation (Swagger): http://localhost:8080/swagger-ui.html


## Testing

To run the automated test suite and check code reliability, navigate to the backend directory and execute:
```bash
./mvnw test
```
