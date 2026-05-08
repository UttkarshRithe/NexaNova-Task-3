# Mock Evaluation System Backend

A production-grade backend for a Mock Evaluation Management System using Spring Boot Microservices Architecture.

## Architecture

- **discovery-service**: Eureka Server for service registration and discovery.
- **config-service**: Spring Cloud Config Server for centralized configuration.
- **api-gateway**: Spring Cloud Gateway for routing and JWT authentication.
- **auth-service**: Authentication service for login and JWT generation.
- **user-service**: Management of users (Admin/Evaluators).
- **batch-service**: Management of batches, technologies, and evaluation rounds.
- **participant-service**: Management of participants and their enrollments in batches.
- **evaluation-service**: Core logic for assigning evaluators to participants and scoring.
- **report-service**: Aggregates data and exports reports in PDF/CSV formats.
- **common-lib**: Shared library for DTOs, exceptions, and utilities.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Cloud 2023.0.0**
- **PostgreSQL 15**
- **Docker & Docker Compose**
- **JWT (jjwt 0.11.5)**
- **MapStruct 1.5.5.Final**
- **iText 7** (PDF Export)
- **Apache Commons CSV** (CSV Export)

## Prerequisites

- Docker and Docker Compose
- Java 17
- Maven 3.8+

## Setup Instructions

1. **Clone the repository.**
2. **Configure Environment Variables:**
   - Rename `.env.template` to `.env` (already provided in the root).
   - Set your database credentials and JWT secret.
3. **Build the Project:**
   ```bash
   mvn clean install -DskipTests
   ```
4. **Run with Docker Compose:**
   ```bash
   docker-compose up --build
   ```
5. **Access Services:**
   - Eureka Dashboard: `http://localhost:8761`
   - Config Server: `http://localhost:8888`
   - API Gateway: `http://localhost:8080`

## API Documentation

- **Auth**: `POST /api/auth/login`
- **Batches**: `GET /api/batches` (Admin)
- **Evaluations**: `GET /api/evaluation-assignments/my` (Evaluator)
- **Reports**: `GET /api/reports/batch/{bId}/technology/{tId}` (Admin)

Refer to the master prompt for the full list of 50+ APIs.

## Database Isolation

Each business service has its own dedicated PostgreSQL database instance as per microservice best practices.

- `batch-db` (Port 5432)
- `user-db` (Port 5433)
- `participant-db` (Port 5434)
- `evaluation-db` (Port 5435)
