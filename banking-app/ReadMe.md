# 🏦 Banking Application — Spring Boot REST API & GraphQL

[![Build Status](https://github.com/vishnureddy1251/banking-app/actions/workflows/ci.yml/badge.svg)](https://github.com/vishnureddy1251/banking-app/actions)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> **Live Demo:** [https://banking-app-ddzo.onrender.com](https://banking-app-ddzo.onrender.com)
>
> **Swagger UI:** [https://banking-app-ddzo.onrender.com/swagger-ui.html](https://banking-app-ddzo.onrender.com/swagger-ui.html)
>
> **GraphiQL:** [https://banking-app-ddzo.onrender.com/graphiql](https://banking-app-ddzo.onrender.com/graphiql)

A production-grade banking backend featuring **REST (v1/v2) + GraphQL** side by side, **JWT authentication**, **6 enterprise resilience patterns**, **event sourcing**, **real-time WebSocket notifications**, **caching**, **rate limiting**, **audit logging**, and **API versioning** — built to demonstrate senior-level Java/Spring Boot skills.

---

## 🏗️ Architecture

```
Client (Postman / Browser / Swagger UI / GraphiQL)
        ↓ HTTP Request
┌──────────────────────────────┐
│      Rate Limiting Filter    │  ← 20 req/min per IP (Bucket4j)
├──────────────────────────────┤
│      JWT Auth Filter         │  ← Validates Bearer token
├──────────────────────────────┤
│   Global Exception Handler   │  ← Consistent error responses
├──────────────────────────────┤
│   REST Controllers (v1/v2)   │  ← Versioned REST endpoints
│   GraphQL Resolvers          │  ← Query & Mutation resolvers
├──────────────────────────────┤
│      Service Layer           │  ← Business logic & caching
├──────────────────────────────┤
│  Resilience4j (CB/Retry/BH)  │  ← Circuit Breaker + Retry + Bulkhead
├──────────────────────────────┤
│      Repository Layer        │  ← Database operations (JPA)
├──────────────────────────────┤
│  Audit Interceptor + Events  │  ← Logs actions + Event Sourcing
├──────────────────────────────┤
│     SQLite Database (File)   │  ← Persistent data storage
└──────────────────────────────┘
```

---

## ✨ Features at a Glance

| Category | Features |
|----------|----------|
| **Core Banking** | Accounts, Customers, Transactions, Loans, Bill Payments, Notifications |
| **API Styles** | REST (v1 + v2 with DTOs) and GraphQL with GraphiQL UI |
| **Security** | JWT authentication, role-based access (ROLE_USER / ROLE_ADMIN), BCrypt password hashing |
| **Resilience** | Circuit Breaker, Retry with Exponential Backoff, Bulkhead Isolation, Timeout (TimeLimiter) |
| **Performance** | Caffeine in-memory caching, write batching (Hibernate batch_size=20), lazy loading |
| **Real-Time** | WebSocket push notifications via SockJS + STOMP |
| **Observability** | Audit logging (auto-intercepts every request), Actuator health endpoints |
| **API Quality** | Rate limiting (Bucket4j), global exception handling (10 HTTP status codes), API versioning |
| **Event Sourcing** | Immutable event store, state rebuild, time-travel queries |
| **Documentation** | Swagger/OpenAPI with JWT auth support |
| **Testing** | 39 unit tests (JUnit 5 + Mockito + AssertJ) |
| **CI/CD** | GitHub Actions pipeline (build + test on every push) |
| **Deployment** | Docker + Docker Compose, deployed on Render |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Programming language |
| Spring Boot 3.5 | Application framework |
| Spring Data JPA | ORM & database access |
| Spring Security | Authentication & authorization |
| JWT (JJWT) | Token-based authentication |
| Spring GraphQL | GraphQL API alongside REST |
| SQLite | Persistent file-based database |
| Resilience4j | Circuit Breaker, Retry, Bulkhead, TimeLimiter |
| Caffeine | In-memory caching |
| Bucket4j | API rate limiting |
| Spring WebSocket | Real-time push notifications |
| Swagger/OpenAPI | Interactive REST API documentation |
| JUnit 5 + Mockito | Unit testing |
| GitHub Actions | CI/CD pipeline |
| Docker | Containerization |
| Render | Cloud deployment |

---

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven (or use the included wrapper)

### Run locally

```bash
git clone https://github.com/vishnureddy1251/banking-app.git
cd banking-app/banking-app
./mvnw spring-boot:run
```

### Open in browser

| Resource | URL |
|----------|-----|
| Application | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| GraphiQL | http://localhost:8080/graphiql |
| Actuator | http://localhost:8080/actuator |
| WebSocket Test | http://localhost:8080/ws-test.html |

### Run with Docker

```bash
cd banking-app/banking-app
docker-compose up --build
```

---

## 🔐 Authentication

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123", "role": "ROLE_ADMIN"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### Use token

```bash
curl -X GET http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer <your-token>"
```

### Role-Based Access

| Role | Permissions |
|------|-------------|
| `ROLE_USER` | View accounts, deposit, withdraw, transfer, pay bills |
| `ROLE_ADMIN` | Everything above + delete accounts, approve/reject loans, view audit logs |

---

## 📡 REST API Endpoints

> Full interactive docs at [Swagger UI](https://banking-app-ddzo.onrender.com/swagger-ui.html)

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login & get JWT |

### Accounts

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Create account |
| GET | `/api/v1/accounts` | Get all accounts |
| GET | `/api/v1/accounts/{id}` | Get by ID |
| PUT | `/api/v1/accounts/{id}/deposit` | Deposit money |
| PUT | `/api/v1/accounts/{id}/withdraw` | Withdraw money |
| POST | `/api/v1/accounts/transfer` | Transfer funds |
| DELETE | `/api/v1/accounts/{id}` | Delete (ADMIN) |

### Accounts v2 (Enhanced DTOs)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v2/accounts` | Formatted balance + status |
| GET | `/api/v2/accounts/{id}` | Enhanced response |
| POST | `/api/v2/accounts/transfer` | Validated transfer request |

### Transactions

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/transactions/{accountId}` | All transactions |
| GET | `/api/v1/transactions/{accountId}?type=DEPOSIT` | Filter by type |

### Customers

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/customers` | Create |
| GET | `/api/v1/customers` | Get all |
| GET | `/api/v1/customers/search?name=arjun` | Search |
| PUT | `/api/v1/customers/{id}` | Update |
| DELETE | `/api/v1/customers/{id}` | Delete |

### Loans

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/loans` | Apply for loan |
| PUT | `/api/v1/loans/{id}/approve` | Approve (ADMIN) |
| PUT | `/api/v1/loans/{id}/reject` | Reject (ADMIN) |
| PUT | `/api/v1/loans/{id}/repay` | Make repayment |

### Bill Payments

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/bills/pay` | Pay a bill |
| GET | `/api/v1/bills/account/{accId}` | Bills for account |
| GET | `/api/v1/bills/track/{referenceNumber}` | Track payment |

### Notifications

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/notifications/{accountId}` | All notifications |
| GET | `/api/v1/notifications/{accountId}/unread` | Unread only |
| PUT | `/api/v1/notifications/{id}/read` | Mark as read |

### Resilience & System

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/circuit-breaker/payment` | Test circuit breaker |
| POST | `/api/v1/circuit-breaker/gateway/down` | Simulate failure |
| GET | `/api/v1/circuit-breaker/status` | View CB states |
| POST | `/api/v1/resilience/payment` | Retry + Bulkhead + CB |
| GET | `/api/v1/resilience/status` | All resilience stats |
| POST | `/api/v1/system/timeout/simulate` | Test timeouts |
| POST | `/api/v1/system/batch/queue` | Test write batching |

### Event Sourcing

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/events/publish` | Publish event |
| GET | `/api/v1/events/account/{id}` | Event history |
| GET | `/api/v1/events/account/{id}/rebuild` | Rebuild state |
| GET | `/api/v1/events/account/{id}/at/{seq}` | Time travel |

### Audit, Cache & Rate Limiting

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/audit` | Recent 50 logs (ADMIN) |
| GET | `/api/v1/cache/stats` | Cache hit/miss stats |
| DELETE | `/api/v1/cache/clear` | Clear all caches |
| GET | `/api/v1/rate-limit/info` | View rate limit policy |

---

## 🔮 GraphQL API

Open [GraphiQL](https://banking-app-ddzo.onrender.com/graphiql) and try:

**The Power Query** — 1 query replaces 3 REST calls:

```graphql
{
  accountById(id: 1) {
    accountName
    balance
    transactions {
      transactionType
      amount
      description
    }
    loans {
      loanType
      loanAmount
      status
    }
  }
}
```

**Create account:**

```graphql
mutation {
  createAccount(input: {
    accountName: "Arjun Don"
    accountType: "SAVINGS"
    balance: 5000
  }) {
    id
    accountNumber
    balance
  }
}
```

---

## 🔄 Enterprise Patterns

### Circuit Breaker
```
🟢 CLOSED    → Normal operation
🔴 OPEN      → Too many failures → fallback response
🟡 HALF-OPEN → Testing recovery
```

### Retry with Exponential Backoff
```
Attempt 1 → Fails → Wait 1s
Attempt 2 → Fails → Wait 2s
Attempt 3 → Fails → Fallback
```

### Bulkhead Isolation
```
Payment  → Max 10 concurrent
Loan     → Max 5 concurrent
Transfer → Max 15 concurrent
```

### Event Sourcing
```
Event 1: ACCOUNT_CREATED  → balance: 0
Event 2: DEPOSITED         → +5000 → balance: 5000
Event 3: WITHDRAWN         → -500  → balance: 4500
→ Rebuild state at ANY point in time
```

### Caching
```
1st GET /accounts/1 → DB query → stored in cache
2nd GET /accounts/1 → returned from cache (no DB hit)
On update           → cache evicted automatically
```

### API Versioning
```
GET /api/v1/accounts/1 → raw entity
GET /api/v2/accounts/1 → DTO with formattedBalance, status, apiVersion
```

---

## 🛡️ Error Handling

All errors return a consistent format:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Account not found with ID: 99",
  "path": "/api/v1/accounts/99",
  "timestamp": "2026-03-21T20:15:00"
}
```

| Status | When |
|--------|------|
| 400 | Validation errors, bad input |
| 401 | Invalid/expired token |
| 403 | Insufficient permissions |
| 404 | Resource not found |
| 405 | Wrong HTTP method |
| 409 | Duplicate resource |
| 422 | Insufficient balance |
| 429 | Rate limit exceeded |
| 503 | External service down |
| 500 | Unexpected error |

---

## 📋 WebSocket Notifications

Real-time push — no polling needed:

1. Open `http://localhost:8080/ws-test.html`
2. Connect to Account ID `1`
3. Make a deposit in Postman → notification appears **instantly** in the browser

Supports: deposit alerts, withdrawal alerts, transfer notifications, loan approvals, bill payments, low balance warnings, and system broadcasts.

---

## 🧪 Testing

```bash
# Run all 39 tests
./mvnw test
```

```
✅ AccountServiceTest  — 26 tests (create, deposit, withdraw, transfer, delete)
✅ AuthServiceTest     — 13 tests (register, login, roles, token generation)
```

---

## 📁 Project Structure

```
banking-app/
├── src/main/java/com/banking/app/
│   ├── config/          SecurityConfig, CacheConfig, WebSocketConfig, SwaggerConfig,
│   │                    AuditInterceptor, RateLimitFilter, WebConfig
│   ├── security/        JwtUtil, JwtAuthFilter, CustomUserDetailsService
│   ├── graphql/         QueryResolver, MutationResolver
│   ├── controller/      14 controllers (REST v1, v2, system, events, cache, resilience)
│   ├── dto/             ApiError, AccountResponseV2, TransferRequestV2
│   ├── exception/       8 custom exceptions + GlobalExceptionHandler
│   ├── model/           9 entities (Account, Transaction, Loan, User, AccountEvent, etc.)
│   ├── repository/      9 JPA repositories
│   └── service/         12 services (Account, Auth, EventSourcing, Resilience, WebSocket, etc.)
├── src/main/resources/
│   ├── application.properties
│   ├── graphql/schema.graphqls
│   └── static/ws-test.html
├── src/test/java/       Unit tests (JUnit 5 + Mockito)
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## 🚢 Deployment

| Platform | Status |
|----------|--------|
| **Render** | ✅ Live at [banking-app-ddzo.onrender.com](https://banking-app-ddzo.onrender.com) |
| **GitHub Actions** | ✅ CI pipeline (build + test on every push) |
| **Docker** | ✅ `docker-compose up --build` |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -m "Add new feature"`)
4. Push to branch (`git push origin feature/new-feature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).