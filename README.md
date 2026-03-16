# 🏦 Banking Application - Spring Boot REST API

A banking REST API built with **Spring Boot**, featuring account management, transactions, loans, bill payments, JWT authentication, circuit breaker pattern, rate limiting, and audit logging.

---

## 🏗️ Architecture

```
Client (Postman / Browser / Swagger UI)
        ↓ HTTP Request
┌──────────────────────────────┐
│      Rate Limiting Filter    │  ← 20 requests/min per IP (Bucket4j)
├──────────────────────────────┤
│      JWT Auth Filter         │  ← Validates Bearer token
├──────────────────────────────┤
│      Controller Layer        │  ← Handles HTTP requests/responses
├──────────────────────────────┤
│      Service Layer           │  ← Business logic & validations
├──────────────────────────────┤
│      Circuit Breaker         │  ← Resilience4j fallback protection
├──────────────────────────────┤
│      Repository Layer        │  ← Database operations (JPA)
├──────────────────────────────┤
│      Audit Interceptor       │  ← Logs every API action
├──────────────────────────────┤
│     SQLite Database (File)   │  ← Persistent data storage
└──────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Technology        | Purpose                          |
|-------------------|----------------------------------|
| Java 17           | Programming language             |
| Spring Boot 3.5   | Application framework            |
| Spring Data JPA   | ORM & database access            |
| Spring Security   | Authentication & authorization   |
| JWT (JJWT)        | Token-based authentication       |
| SQLite            | Persistent file-based database   |
| Resilience4j      | Circuit breaker pattern          |
| Bucket4j          | API rate limiting                |
| Swagger/OpenAPI   | Interactive API documentation    |
| Lombok            | Reduces boilerplate code         |
| Maven             | Build & dependency management    |

---

## 🚀 How to Run

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/banking-app.git
cd banking-app

# Run the application
./mvnw spring-boot:run
```

- **Application:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Database:** SQLite file stored at `bankingdb.db` in project root
- **View data:** Use [DB Browser for SQLite](https://sqlitebrowser.org/dl/) to open `bankingdb.db`

> Data persists even after restarting the application.

---

## 🔐 Authentication

This app uses **JWT (JSON Web Token)** authentication. You must register and login to access protected endpoints.

### Step 1: Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "arjun", "password": "pass123", "role": "ROLE_ADMIN"}'
```

### Step 2: Login (get token)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "arjun", "password": "pass123"}'
```

### Step 3: Use token in all requests
```bash
curl -X GET http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer <your-token-here>"
  
```

### Role-Based Access

| Role       | Permissions                                          |
|------------|------------------------------------------------------|
| ROLE_USER  | View accounts, deposit, withdraw, transfer, pay bills |
| ROLE_ADMIN | Everything above + delete accounts, approve/reject loans, view audit logs |

---

## 📡 API Endpoints

### Authentication (Public - no token needed)

| Method | Endpoint              | Description         |
|--------|-----------------------|---------------------|
| POST   | `/api/auth/register`  | Register new user   |
| POST   | `/api/auth/login`     | Login & get token   |

### Accounts

| Method | Endpoint                              | Description                |
|--------|---------------------------------------|----------------------------|
| POST   | `/api/v1/accounts`                    | Create new account         |
| GET    | `/api/v1/accounts`                    | Get all accounts           |
| GET    | `/api/v1/accounts/{id}`               | Get account by ID          |
| PUT    | `/api/v1/accounts/{id}/deposit`       | Deposit money              |
| PUT    | `/api/v1/accounts/{id}/withdraw`      | Withdraw money             |
| POST   | `/api/v1/accounts/transfer`           | Transfer between accounts  |
| DELETE | `/api/v1/accounts/{id}`               | Delete account (ADMIN)     |

### Transactions

| Method | Endpoint                                        | Description                    |
|--------|-------------------------------------------------|--------------------------------|
| GET    | `/api/v1/transactions/{accountId}`              | Get all transactions           |
| GET    | `/api/v1/transactions/{accountId}?type=DEPOSIT` | Filter by type                 |

### Customers

| Method | Endpoint                                  | Description              |
|--------|-------------------------------------------|--------------------------|
| POST   | `/api/v1/customers`                       | Create customer          |
| GET    | `/api/v1/customers`                       | Get all customers        |
| GET    | `/api/v1/customers/{id}`                  | Get customer by ID       |
| GET    | `/api/v1/customers/search?name=arjun`     | Search by name           |
| PUT    | `/api/v1/customers/{id}`                  | Update customer profile  |
| DELETE | `/api/v1/customers/{id}`                  | Delete customer          |

### Loans

| Method | Endpoint                           | Description                  |
|--------|------------------------------------|------------------------------|
| POST   | `/api/v1/loans`                    | Apply for a loan             |
| GET    | `/api/v1/loans`                    | Get all loans                |
| GET    | `/api/v1/loans/{id}`               | Get loan by ID               |
| GET    | `/api/v1/loans/account/{accId}`    | Get loans for account        |
| GET    | `/api/v1/loans/status/{status}`    | Filter by status             |
| PUT    | `/api/v1/loans/{id}/approve`       | Approve loan (ADMIN)         |
| PUT    | `/api/v1/loans/{id}/reject`        | Reject loan (ADMIN)          |
| PUT    | `/api/v1/loans/{id}/repay`         | Make a repayment             |

### Bill Payments

| Method | Endpoint                                             | Description                   |
|--------|------------------------------------------------------|-------------------------------|
| POST   | `/api/v1/bills/pay`                                  | Pay a bill                    |
| GET    | `/api/v1/bills/account/{accId}`                      | Get all payments for account  |
| GET    | `/api/v1/bills/account/{accId}?type=ELECTRICITY`     | Filter by bill type           |
| GET    | `/api/v1/bills/track/{referenceNumber}`               | Track payment by reference    |

### Notifications

| Method | Endpoint                                       | Description              |
|--------|-------------------------------------------------|--------------------------|
| GET    | `/api/v1/notifications/{accountId}`             | All notifications        |
| GET    | `/api/v1/notifications/{accountId}/unread`      | Unread only              |
| GET    | `/api/v1/notifications/{accountId}/count`       | Unread count             |
| PUT    | `/api/v1/notifications/{id}/read`               | Mark one as read         |
| PUT    | `/api/v1/notifications/{accountId}/read-all`    | Mark all as read         |

### Circuit Breaker (Public - demo endpoints)

| Method | Endpoint                                  | Description                          |
|--------|-------------------------------------------|--------------------------------------|
| POST   | `/api/v1/circuit-breaker/payment`         | Test payment with circuit breaker    |
| POST   | `/api/v1/circuit-breaker/loan/{loanId}`   | Test loan approval with fallback     |
| POST   | `/api/v1/circuit-breaker/transfer`        | Test external transfer with fallback |
| POST   | `/api/v1/circuit-breaker/gateway/down`    | Simulate gateway going DOWN          |
| POST   | `/api/v1/circuit-breaker/gateway/up`      | Simulate gateway recovering          |
| GET    | `/api/v1/circuit-breaker/gateway/status`  | Check gateway status                 |
| GET    | `/api/v1/circuit-breaker/status`          | View all circuit breaker states      |

### Audit Logs (ADMIN only)

| Method | Endpoint                                               | Description          |
|--------|--------------------------------------------------------|----------------------|
| GET    | `/api/v1/audit`                                        | Recent 50 logs       |
| GET    | `/api/v1/audit/user/{username}`                        | Logs by user         |
| GET    | `/api/v1/audit/action/{action}`                        | Logs by action type  |
| GET    | `/api/v1/audit/entity/{type}`                          | Logs by entity type  |
| GET    | `/api/v1/audit/date?start=2025-01-01&end=2025-01-31`  | Logs by date range   |

### Rate Limiting

| Method | Endpoint                      | Description            |
|--------|-------------------------------|------------------------|
| GET    | `/api/v1/rate-limit/info`     | View rate limit policy |

> All API responses include `X-Rate-Limit-Remaining` and `X-Rate-Limit-Limit` headers. Exceeding 20 requests/minute returns `429 Too Many Requests`.

---

## 🔄 Circuit Breaker Pattern

The app uses **Resilience4j** to protect against external service failures.

```
🟢 CLOSED    → Normal operation, requests pass through
🔴 OPEN      → Too many failures, fallback response returned instantly
🟡 HALF-OPEN → Testing if service recovered
```

**Try it:**
1. `POST /api/v1/circuit-breaker/gateway/down` → Simulate gateway failure
2. `POST /api/v1/circuit-breaker/payment` → Hit 5 times → See fallback activate
3. `GET /api/v1/circuit-breaker/status` → See circuit state change to OPEN
4. `POST /api/v1/circuit-breaker/gateway/up` → Recover gateway
5. Wait 10 seconds → Circuit goes HALF-OPEN → CLOSED

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
│   │   ├── AuditInterceptor.java             ← Auto-logs every API request
│   │   ├── WebConfig.java                    ← Registers interceptors
│   │   └── RateLimitFilter.java              ← 20 req/min per IP
│   ├── security/
│   │   ├── JwtUtil.java                      ← Generate & validate JWT tokens
│   │   ├── JwtAuthFilter.java                ← Checks token on every request
│   │   └── CustomUserDetailsService.java     ← Loads user from DB
│   ├── controller/
│   │   ├── AccountController.java
│   │   ├── AuditLogController.java
│   │   ├── AuthController.java
│   │   ├── BillPaymentController.java
│   │   ├── CircuitBreakerController.java
│   │   ├── CustomerController.java
│   │   ├── LoanController.java
│   │   ├── NotificationController.java
│   │   ├── RateLimitController.java
│   │   └── TransactionController.java
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── InsufficientBalanceException.java
│   ├── model/
│   │   ├── Account.java
│   │   ├── AuditLog.java
│   │   ├── BillPayment.java
│   │   ├── Customer.java
│   │   ├── Loan.java
│   │   ├── Notification.java
│   │   ├── Transaction.java
│   │   └── User.java
│   ├── repository/
│   │   ├── AccountRepository.java
│   │   ├── AuditLogRepository.java
│   │   ├── BillPaymentRepository.java
│   │   ├── CustomerRepository.java
│   │   ├── LoanRepository.java
│   │   ├── NotificationRepository.java
│   │   ├── TransactionRepository.java
│   │   └── UserRepository.java
│   └── service/
│       ├── AccountService.java
│       ├── AuditLogService.java
│       ├── AuthService.java
│       ├── BillPaymentService.java
│       ├── CircuitBreakerService.java
│       ├── CustomerService.java
│       ├── LoanService.java
│       ├── NotificationService.java
│       ├── PaymentGatewayService.java
│       └── TransactionService.java
└── src/main/resources/
    └── application.properties
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
6. 

---
