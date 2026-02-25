# MedApp Backend (Modular Monolith)

Production-oriented Spring Boot 3 backend for a hyperlocal medicine delivery marketplace.

## Stack
- Java 21
- Spring Boot 3.x
- Spring Security (JWT access + refresh)
- MySQL
- Redis (cache + rate limiting + inventory lock coordination)
- JPA/Hibernate
- Flyway migrations
- OpenAPI/Swagger
- Docker/Docker Compose

## Modules
- `auth`
- `user`
- `pharmacy`
- `medicine`
- `inventory`
- `prescription`
- `cart`
- `order`
- `payment`
- `delivery`
- `notification`
- `settlement`
- `audit`
- `common`
- `config`

## Run Locally

```bash
mvn clean test
mvn spring-boot:run
```

Default local DB connection:
- URL: `jdbc:mysql://localhost:3306/medapp?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- Username: `root`
- Password: `12345678`

## Run with Docker

```bash
docker-compose up --build
```

Docker MySQL is exposed on host port `3307` with password `12345678`.

App: `http://localhost:8080`
Swagger: `http://localhost:8080/swagger-ui.html`

## Seed Data

Flyway seeds admin/user/pharmacy/rider users, one approved pharmacy, medicines, inventory, and a default user address.

## Security
- OTP login (mockable)
- JWT access + refresh token flow
- Refresh token persistence and revocation
- Access-token revocation list support
- RBAC (`ROLE_USER`, `ROLE_PHARMACY`, `ROLE_RIDER`, `ROLE_ADMIN`)
- Input validation and centralized exception handling
- Rate limiting and CORS controls

## Notes
- Razorpay integration is abstracted behind `PaymentGateway` and represented via `RazorpayPaymentGateway` service hooks.
- Notifications are async and event-driven.
- Order lifecycle is governed by a strict state machine in `OrderStateMachine`.
