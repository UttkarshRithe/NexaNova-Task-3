# EvalTrack: Mock Evaluation System

A robust, production-grade backend for the **NexaNova Mock Evaluation Management System**, engineered with a modern Spring Boot Microservices Architecture. 

EvalTrack is designed to streamline technical training academies by automating the entire lifecycle of mock evaluations. From organizing training batches and assigning evaluators to multi-round scoring and generating AI-powered holistic performance reports, this system provides a highly scalable and fault-tolerant foundation.

---

## 🚀 Key Features & Core Workflows

### 1. Administrative Management
- **Batch & Technology Control:** Admins can create training batches, define technology stacks (e.g., Java, Python, React), and configure the specific number of evaluation rounds required for each technology.
- **Participant Enrollment:** Admins manage participant profiles and formally enroll them into specific batch-technologies.
- **Evaluator Assignment:** Admins assign qualified evaluators to specific participants for specific rounds to ensure unbiased scoring and even workload distribution.

### 2. Evaluator Operations
- **Assignment Tracking:** Evaluators receive personalized dashboards via the API to view their pending participant evaluation assignments.
- **Dynamic Scoring:** Evaluators input scores (out of 10) and qualitative feedback for each round they conduct.

### 3. Reporting & AI Integration
- **Aggregated Reports:** The system mathematically compiles scores across all rounds for a participant to generate a holistic view of their performance.
- **AI-Powered Insights:** Integrated with OpenRouter (DeepSeek V3), the AI service analyzes the evaluator feedback and scores to generate comprehensive, human-like summaries, strengths, and targeted improvement strategies for each participant.
- **Document Exporting:** Admins can export detailed Batch and Participant analytical reports in standard PDF and CSV formats.

### 4. Asynchronous Notifications
- **Event-Driven Emails:** Utilizing RabbitMQ, the system automatically triggers background notification events (e.g., when a participant is enrolled or an evaluation is completed) without blocking the main application threads.

---

## 🏗️ Microservices Architecture

The system strictly adheres to Domain-Driven Design (DDD), decomposing the domain into highly cohesive, loosely coupled microservices:

### Core Infrastructure
- **discovery-service**: Eureka Server for dynamic service registration and client-side load balancing.
- **config-service**: Spring Cloud Config Server providing centralized, externalized configuration management across all environments.
- **api-gateway**: Spring Cloud Gateway acting as the single external entry point. It handles request routing, Redis-backed IP rate-limiting, and global JWT authentication enforcement.

### Business Domains
- **auth-service**: Dedicated authentication authority for secure login, BCrypt password verification, and JWT token generation.
- **user-service**: Management of system identities and roles (Admin vs. Evaluator) featuring optimized, paginated record listings.
- **batch-service**: The source of truth for training batches, curriculum technologies, and evaluation configurations.
- **participant-service**: Manages participant profiles and their lifecycle enrollments. Emits AMQP events to the message broker upon state changes.
- **evaluation-service**: The orchestration layer for assigning evaluators and recording scores. Features robust Feign Client fallbacks to handle downstream network failures.
- **report-service**: Aggregates distributed scoring data across multiple services to construct and export visual PDF/CSV reports.
- **ai-service**: A reactive, WebFlux-driven service that asynchronously communicates with external LLM providers (OpenRouter) to generate performance insights.
- **notification-service**: A consumer service that constantly listens to RabbitMQ queues (`evaltrack.exchange`) and dispatches responsive email notifications.
- **common-lib**: Shared Maven library standardizing DTOs, cross-cutting global exceptions, and security utilities to eliminate code duplication.

---

## 🛠️ Tech Stack & Resilience Patterns

### Core Frameworks
- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Cloud 2023.0.0** (Eureka, Config, Gateway, OpenFeign)

### Persistence & Message Brokering
- **PostgreSQL 15**: Dedicated database instances per service to enforce strict microservice data isolation.
- **Redis**: In-memory data structure store utilizing Spring Data Redis Reactive for Gateway Request Rate Limiting.
- **RabbitMQ**: Advanced Message Queuing Protocol broker for decoupled inter-service communication.

### Advanced Resilience Mechanisms
- **Circuit Breakers & Fallbacks**: Implementation of OpenFeign fallbacks. If a service (e.g., `user-service`) goes offline, the `evaluation-service` degrades gracefully, returning safe empty states rather than cascading `500 Internal Server Error`s.
- **API Rate Limiting**: Token-bucket algorithm applied at the Gateway level to prevent volumetric abuse and brute-forcing.
- **Container Health Checks**: Docker Compose is configured with native `pg_isready` health checks, ensuring Spring Boot applications only boot once their databases are fully initialized, eliminating startup race conditions.

---

## ⚙️ Setup Instructions

### Prerequisites
- Docker and Docker Compose
- Java 17 (for local development)
- Maven 3.8+ (for local development)

### Running the Cluster

1. **Clone the repository.**
2. **Configure Environment Variables:**
   - Create a `.env` file in the root directory (use `.env.template` as a baseline).
   - Ensure you strictly define your `DB_PASSWORD`, `JWT_SECRET` (min 32 chars), and `OPENROUTER_API_KEY`.
3. **Build the Project:**
   Execute a full Maven build to compile the modules and package the `.jar` files:
   ```bash
   mvn clean package -DskipTests
   ```
4. **Run with Docker Compose:**
   Spin up the entire infrastructure (databases, brokers, and all 11 microservices):
   ```bash
   docker-compose up --build -d
   ```
   *Note: The system utilizes `service_healthy` conditions. Please allow up to 60 seconds for all Spring Boot services to detect their databases and boot sequentially.*

### Access Points
- **API Gateway (Main Entrypoint):** `http://localhost:9900`
- **Eureka Dashboard:** `http://localhost:8761`
- **Config Server:** `http://localhost:8888`
- **RabbitMQ Management:** `http://localhost:15672` (if port mapped)

---

## 📚 API Documentation Snapshot

All API traffic must be routed through the API Gateway at `localhost:9900` and authenticated using a Bearer token.

- **Authentication**: `POST /api/auth/login`
- **Batches**: `GET /api/batches` (Admin Only)
- **Evaluations**: `GET /api/evaluation-assignments/my` (Evaluator Access)
- **Reports**: `GET /api/reports/batch/{batchId}/technology/{techId}` (Admin Only)
- **AI Summary**: `GET /api/ai/analyze/participant/{participantId}`

*(Note: Refer to the comprehensive Postman collection or OpenAPI spec for the full list of 50+ endpoints).*
