# 🏦 Banking Application - Spring Boot REST API & GraphQL

A comprehensive, production-grade banking API built with **Spring Boot**, featuring REST & GraphQL endpoints, JWT authentication, enterprise resilience patterns (Circuit Breaker, Retry, Bulkhead), event sourcing, caching, rate limiting, audit logging, and API versioning.

---

## 🏗️ Architecture

```
Client (Postman / Browser / Swagger UI / GraphiQL)
        ↓ HTTP Request
┌──────────────────────────────┐
│      Rate Limiting Filter    │  ← 20 requests/min per IP (Bucket4j)
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

## 🛠️ Tech Stack

| Technology         | Purpose                              |
|--------------------|--------------------------------------|
| Java 17            | Programming language                 |
| Spring Boot 3.5    | Application framework                |
| Spring Data JPA    | ORM & database access                |
| Spring Security    | Authentication & authorization       |
| JWT (JJWT)         | Token-based authentication           |
| Spring GraphQL     | GraphQL API alongside REST           |
| SQLite             | Persistent file-based database       |
| Resilience4j       | Circuit Breaker, Retry, Bulkhead     |
| Caffeine           | In-memory caching                    |
| Bucket4j           | API rate limiting                    |
| Swagger/OpenAPI    | Interactive REST API documentation   |
| Lombok             | Reduces boilerplate code             |
| Maven              | Build & dependency management        |

---

## 🚀 How to Run

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/banking-app.git
cd banking-app

# Run the application
./mvnw spring-boot:run
```

| Resource            | URL                                        |
|---------------------|--------------------------------------------|
| Application         | http://localhost:8080                       |
| Swagger UI (REST)   | http://localhost:8080/swagger-ui.html       |
| GraphiQL (GraphQL)  | http://localhost:8080/graphiql              |
| Actuator            | http://localhost:8080/actuator              |
| Database            | `bankingdb.db` in project root             |

> Data persists across restarts. Use [DB Browser for SQLite](https://sqlitebrowser.org/dl/) to view data.

---

## 🔐 Authentication (JWT)

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123", "role": "ROLE_ADMIN"}'
```

### Login (get token)
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

| Role       | Permissions                                                    |
|------------|----------------------------------------------------------------|
| ROLE_USER  | View accounts, deposit, withdraw, transfer, pay bills          |
| ROLE_ADMIN | Everything above + delete accounts, approve/reject loans, audit logs |

---

## 📡 REST API Endpoints

### Authentication (Public)

| Method | Endpoint              | Description       |
|--------|-----------------------|-------------------|
| POST   | `/api/auth/register`  | Register user     |
| POST   | `/api/auth/login`     | Login & get token |

### Accounts (v1)

| Method | Endpoint                         | Description            |
|--------|----------------------------------|------------------------|
| POST   | `/api/v1/accounts`               | Create account         |
| GET    | `/api/v1/accounts`               | Get all accounts       |
| GET    | `/api/v1/accounts/{id}`          | Get account by ID      |
| PUT    | `/api/v1/accounts/{id}/deposit`  | Deposit money          |
| PUT    | `/api/v1/accounts/{id}/withdraw` | Withdraw money         |
| POST   | `/api/v1/accounts/transfer`      | Transfer funds         |
| DELETE | `/api/v1/accounts/{id}`          | Delete account (ADMIN) |

### Accounts (v2 - Enhanced with DTOs)

| Method | Endpoint                          | Description                      |
|--------|-----------------------------------|----------------------------------|
| GET    | `/api/v2/accounts`                | All accounts (formatted balance) |
| GET    | `/api/v2/accounts/{id}`           | Account with status & formatting |
| POST   | `/api/v2/accounts`                | Create account (DTO response)    |
| POST   | `/api/v2/accounts/transfer`       | Transfer with validated request  |

### Transactions

| Method | Endpoint                                        | Description          |
|--------|-------------------------------------------------|----------------------|
| GET    | `/api/v1/transactions/{accountId}`              | All transactions     |
| GET    | `/api/v1/transactions/{accountId}?type=DEPOSIT` | Filter by type       |

### Customers

| Method | Endpoint                              | Description       |
|--------|---------------------------------------|-------------------|
| POST   | `/api/v1/customers`                   | Create customer   |
| GET    | `/api/v1/customers`                   | Get all           |
| GET    | `/api/v1/customers/{id}`              | Get by ID         |
| GET    | `/api/v1/customers/search?name=arjun` | Search by name    |
| PUT    | `/api/v1/customers/{id}`              | Update            |
| DELETE | `/api/v1/customers/{id}`              | Delete            |

### Loans

| Method | Endpoint                        | Description            |
|--------|---------------------------------|------------------------|
| POST   | `/api/v1/loans`                 | Apply for loan         |
| GET    | `/api/v1/loans`                 | Get all loans          |
| GET    | `/api/v1/loans/{id}`            | Get by ID              |
| GET    | `/api/v1/loans/account/{accId}` | Loans for account      |
| GET    | `/api/v1/loans/status/{status}` | Filter by status       |
| PUT    | `/api/v1/loans/{id}/approve`    | Approve (ADMIN)        |
| PUT    | `/api/v1/loans/{id}/reject`     | Reject (ADMIN)         |
| PUT    | `/api/v1/loans/{id}/repay`      | Repay loan             |

### Bill Payments

| Method | Endpoint                                         | Description         |
|--------|--------------------------------------------------|---------------------|
| POST   | `/api/v1/bills/pay`                              | Pay a bill          |
| GET    | `/api/v1/bills/account/{accId}`                  | Bills for account   |
| GET    | `/api/v1/bills/account/{accId}?type=ELECTRICITY` | Filter by type      |
| GET    | `/api/v1/bills/track/{referenceNumber}`           | Track by reference  |

### Notifications

| Method | Endpoint                                     | Description      |
|--------|----------------------------------------------|------------------|
| GET    | `/api/v1/notifications/{accountId}`          | All              |
| GET    | `/api/v1/notifications/{accountId}/unread`   | Unread only      |
| GET    | `/api/v1/notifications/{accountId}/count`    | Unread count     |
| PUT    | `/api/v1/notifications/{id}/read`            | Mark one read    |
| PUT    | `/api/v1/notifications/{accountId}/read-all` | Mark all read    |

### Circuit Breaker (Public Demo)

| Method | Endpoint                                 | Description                     |
|--------|------------------------------------------|---------------------------------|
| POST   | `/api/v1/circuit-breaker/payment`        | Test payment with CB            |
| POST   | `/api/v1/circuit-breaker/loan/{loanId}`  | Test loan with CB               |
| POST   | `/api/v1/circuit-breaker/transfer`       | Test transfer with CB           |
| POST   | `/api/v1/circuit-breaker/gateway/down`   | Simulate gateway DOWN           |
| POST   | `/api/v1/circuit-breaker/gateway/up`     | Simulate gateway UP             |
| GET    | `/api/v1/circuit-breaker/status`         | View CB states                  |

### Resilience (Retry + Bulkhead + CB combined)

| Method | Endpoint                              | Description                  |
|--------|---------------------------------------|------------------------------|
| POST   | `/api/v1/resilience/payment`          | Payment with all 3 patterns  |
| POST   | `/api/v1/resilience/transfer`         | Transfer with all 3 patterns |
| POST   | `/api/v1/resilience/loan/{loanId}`    | Loan with bulkhead + CB      |
| GET    | `/api/v1/resilience/status`           | All resilience stats         |

### Event Sourcing

| Method | Endpoint                                         | Description                |
|--------|--------------------------------------------------|----------------------------|
| POST   | `/api/v1/events/publish`                         | Publish event              |
| GET    | `/api/v1/events/account/{accountId}`             | Full event history         |
| GET    | `/api/v1/events/account/{accountId}/rebuild`     | Rebuild state from events  |
| GET    | `/api/v1/events/account/{accountId}/at/{seq}`    | State at specific point    |
| GET    | `/api/v1/events/account/{accountId}/type/{type}` | Events by type             |

### Cache

| Method | Endpoint                 | Description       |
|--------|--------------------------|-------------------|
| GET    | `/api/v1/cache/stats`    | Cache hit/miss    |
| DELETE | `/api/v1/cache/clear`    | Clear all caches  |

### Audit Logs (ADMIN only)

| Method | Endpoint                                              | Description      |
|--------|-------------------------------------------------------|------------------|
| GET    | `/api/v1/audit`                                       | Recent 50 logs   |
| GET    | `/api/v1/audit/user/{username}`                       | Logs by user     |
| GET    | `/api/v1/audit/action/{action}`                       | Logs by action   |
| GET    | `/api/v1/audit/entity/{type}`                         | Logs by entity   |
| GET    | `/api/v1/audit/date?start=2025-01-01&end=2025-01-31` | Logs by date     |

### Rate Limiting

| Method | Endpoint                  | Description        |
|--------|---------------------------|--------------------|
| GET    | `/api/v1/rate-limit/info` | View policy        |

> All responses include `X-Rate-Limit-Remaining` header. Exceeding 20 req/min returns `429`.

---

## 🔮 GraphQL API

Open **http://localhost:8080/graphiql** for the interactive playground.

### Why GraphQL alongside REST?

| Scenario                           | REST             | GraphQL              |
|------------------------------------|------------------|----------------------|
| Get account + transactions + loans | 3 API calls      | 1 query              |
| Get only account name & balance    | Returns all fields | Returns only requested |
| Add new field to response          | New API version  | Just add to query    |

### Example Queries

**Fetch account with nested transactions & loans (1 query = 3 REST calls):**
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

**Transfer money:**
```graphql
mutation {
  transfer(input: {
    fromAccountId: 1
    toAccountId: 2
    amount: 500
  }) {
    message
    amount
  }
}
```

---

## 🔄 Enterprise Patterns Implemented

### 1. Circuit Breaker (Resilience4j)
```
🟢 CLOSED    → Normal, requests pass through
🔴 OPEN      → Too many failures, fallback returned instantly
🟡 HALF-OPEN → Testing if service recovered
```

### 2. Retry with Exponential Backoff
```
Attempt 1 → Fails → Wait 1s
Attempt 2 → Fails → Wait 2s
Attempt 3 → Fails → Fallback response
```

### 3. Bulkhead Isolation
```
Payment service  → Max 10 concurrent requests
Loan service     → Max 5 concurrent requests
Transfer service → Max 15 concurrent requests
```

### 4. Event Sourcing
```
Event 1: ACCOUNT_CREATED  → balance: 0
Event 2: DEPOSITED         → +5000, balance: 5000
Event 3: WITHDRAWN         → -500,  balance: 4500
Event 4: TRANSFERRED_OUT   → -1000, balance: 3500
→ Can rebuild state at ANY point in time
```

### 5. Caching (Caffeine)
```
1st GET /accounts/1 → Database query → Stored in cache
2nd GET /accounts/1 → Returned from cache instantly (no DB)
On update           → Cache cleared automatically
```

### 6. API Versioning
```
GET /api/v1/accounts/1 → Raw entity response
GET /api/v2/accounts/1 → Enhanced DTO with formatted balance & status
```

---

## 🛡️ Global Exception Handling

All errors return a consistent JSON format:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Account not found with ID: 99",
  "path": "/api/v1/accounts/99",
  "timestamp": "2026-02-22T20:15:00"
}
```

| Status | When                              |
|--------|-----------------------------------|
| 400    | Validation errors, bad input      |
| 401    | Invalid/expired token             |
| 403    | No permission (wrong role)        |
| 404    | Resource not found                |
| 405    | Wrong HTTP method                 |
| 409    | Duplicate resource                |
| 422    | Insufficient balance              |
| 429    | Rate limit exceeded               |
| 503    | External service unavailable      |
| 500    | Unexpected server error           |

---

## 📁 Project Structure

```
banking-app/
├── pom.xml
├── bankingdb.db
├── src/main/java/com/banking/app/
│   ├── BankingAppApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java               ← JWT + CORS + role-based access
│   │   ├── SwaggerConfig.java                ← OpenAPI documentation
│   │   ├── CacheConfig.java                  ← Caffeine cache setup
│   │   ├── AuditInterceptor.java             ← Auto-logs every API request
│   │   ├── WebConfig.java                    ← Registers interceptors
│   │   └── RateLimitFilter.java              ← 20 req/min per IP
│   ├── security/
│   │   ├── JwtUtil.java                      ← Generate & validate JWT tokens
│   │   ├── JwtAuthFilter.java                ← Checks token on every request
│   │   └── CustomUserDetailsService.java     ← Loads user from DB
│   ├── graphql/
│   │   ├── QueryResolver.java                ← GraphQL read operations
│   │   └── MutationResolver.java             ← GraphQL write operations
│   ├── controller/
│   │   ├── AccountController.java            ← REST v1
│   │   ├── AccountControllerV2.java          ← REST v2 (DTOs)
│   │   ├── AuditLogController.java
│   │   ├── AuthController.java
│   │   ├── BillPaymentController.java
│   │   ├── CacheController.java
│   │   ├── CircuitBreakerController.java
│   │   ├── CustomerController.java
│   │   ├── EventSourcingController.java
│   │   ├── LoanController.java
│   │   ├── NotificationController.java
│   │   ├── RateLimitController.java
│   │   ├── ResilienceController.java
│   │   └── TransactionController.java
│   ├── dto/
│   │   ├── ApiError.java                     ← Standard error response
│   │   ├── AccountResponseV2.java            ← V2 response DTO
│   │   └── TransferRequestV2.java            ← V2 request with validation
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── BadRequestException.java
│   │   ├── DuplicateResourceException.java
│   │   ├── GlobalExceptionHandler.java       ← Catches all exceptions
│   │   ├── InsufficientBalanceException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── ServiceUnavailableException.java
│   │   └── UnauthorizedException.java
│   ├── model/
│   │   ├── Account.java
│   │   ├── AccountEvent.java                 ← Event sourcing entity
│   │   ├── AuditLog.java
│   │   ├── BillPayment.java
│   │   ├── Customer.java
│   │   ├── Loan.java
│   │   ├── Notification.java
│   │   ├── Transaction.java
│   │   └── User.java
│   ├── repository/
│   │   ├── AccountEventRepository.java
│   │   ├── AccountRepository.java
│   │   ├── AuditLogRepository.java
│   │   ├── BillPaymentRepository.java
│   │   ├── CustomerRepository.java
│   │   ├── LoanRepository.java
│   │   ├── NotificationRepository.java
│   │   ├── TransactionRepository.java
│   │   └── UserRepository.java
│   └── service/
│       ├── AccountService.java               ← With @Cacheable
│       ├── AuditLogService.java
│       ├── AuthService.java
│       ├── BillPaymentService.java
│       ├── CircuitBreakerService.java
│       ├── CustomerService.java
│       ├── EventSourcingService.java
│       ├── LoanService.java
│       ├── NotificationService.java
│       ├── PaymentGatewayService.java
│       ├── ResilienceService.java
│       └── TransactionService.java
└── src/main/resources/
    ├── application.properties
    └── graphql/
        └── schema.graphqls                   ← GraphQL schema definition
```

---

## 🔮 Upcoming Features

- [ ] Docker Containerization
- [ ] CI/CD with GitHub Actions

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