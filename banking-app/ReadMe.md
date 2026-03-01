# 🏦 Banking Application - Spring Boot REST API

A banking REST API built with **Spring Boot**, featuring account management, transactions, loans, bill payments, JWT authentication, circuit breaker pattern, rate limiting, and audit logging.

---

🏗️ Architecture
Client (Postman / Browser / Swagger UI / GraphiQL)
↓ HTTP Request
┌──────────────────────────────┐
│      Rate Limiting Filter    │  ← 20 requests/min per IP (Bucket4j)
├──────────────────────────────┤
│      JWT Auth Filter         │  ← Validates Bearer token
├──────────────────────────────┤
│      Global Exception Handler│  ← Consistent error responses
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

🛠️ Tech Stack
TechnologyPurposeJava 17Programming languageSpring Boot 3.5Application frameworkSpring Data JPAORM & database accessSpring SecurityAuthentication & authorizationJWT (JJWT)Token-based authenticationSpring GraphQLGraphQL API alongside RESTSQLitePersistent file-based databaseResilience4jCircuit Breaker, Retry, BulkheadCaffeineIn-memory cachingBucket4jAPI rate limitingSwagger/OpenAPIInteractive REST API documentationLombokReduces boilerplate codeMavenBuild & dependency management

🚀 How to Run
bash# Clone the repository
git clone https://github.com/YOUR_USERNAME/banking-app.git
cd banking-app

# Run the application
./mvnw spring-boot:run
ResourceURLApplicationhttp://localhost:8080Swagger UI (REST)http://localhost:8080/swagger-ui.htmlGraphiQL (GraphQL)http://localhost:8080/graphiqlActuatorhttp://localhost:8080/actuatorDatabasebankingdb.db in project root

Data persists across restarts. Use DB Browser for SQLite to view data.


🔐 Authentication (JWT)
Register
bashcurl -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"username": "admin", "password": "admin123", "role": "ROLE_ADMIN"}'
Login (get token)
bashcurl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"username": "admin", "password": "admin123"}'
Use token
bashcurl -X GET http://localhost:8080/api/v1/accounts \
-H "Authorization: Bearer <your-token>"
Role-Based Access
RolePermissionsROLE_USERView accounts, deposit, withdraw, transfer, pay billsROLE_ADMINEverything above + delete accounts, approve/reject loans, audit logs

📡 REST API Endpoints
Authentication (Public)
MethodEndpointDescriptionPOST/api/auth/registerRegister userPOST/api/auth/loginLogin & get token
Accounts (v1)
MethodEndpointDescriptionPOST/api/v1/accountsCreate accountGET/api/v1/accountsGet all accountsGET/api/v1/accounts/{id}Get account by IDPUT/api/v1/accounts/{id}/depositDeposit moneyPUT/api/v1/accounts/{id}/withdrawWithdraw moneyPOST/api/v1/accounts/transferTransfer fundsDELETE/api/v1/accounts/{id}Delete account (ADMIN)
Accounts (v2 - Enhanced with DTOs)
MethodEndpointDescriptionGET/api/v2/accountsAll accounts (formatted balance)GET/api/v2/accounts/{id}Account with status & formattingPOST/api/v2/accountsCreate account (DTO response)POST/api/v2/accounts/transferTransfer with validated request
Transactions
MethodEndpointDescriptionGET/api/v1/transactions/{accountId}All transactionsGET/api/v1/transactions/{accountId}?type=DEPOSITFilter by type
Customers
MethodEndpointDescriptionPOST/api/v1/customersCreate customerGET/api/v1/customersGet allGET/api/v1/customers/{id}Get by IDGET/api/v1/customers/search?name=arjunSearch by namePUT/api/v1/customers/{id}UpdateDELETE/api/v1/customers/{id}Delete
Loans
MethodEndpointDescriptionPOST/api/v1/loansApply for loanGET/api/v1/loansGet all loansGET/api/v1/loans/{id}Get by IDGET/api/v1/loans/account/{accId}Loans for accountGET/api/v1/loans/status/{status}Filter by statusPUT/api/v1/loans/{id}/approveApprove (ADMIN)PUT/api/v1/loans/{id}/rejectReject (ADMIN)PUT/api/v1/loans/{id}/repayRepay loan
Bill Payments
MethodEndpointDescriptionPOST/api/v1/bills/payPay a billGET/api/v1/bills/account/{accId}Bills for accountGET/api/v1/bills/account/{accId}?type=ELECTRICITYFilter by typeGET/api/v1/bills/track/{referenceNumber}Track by reference
Notifications
MethodEndpointDescriptionGET/api/v1/notifications/{accountId}AllGET/api/v1/notifications/{accountId}/unreadUnread onlyGET/api/v1/notifications/{accountId}/countUnread countPUT/api/v1/notifications/{id}/readMark one readPUT/api/v1/notifications/{accountId}/read-allMark all read
Circuit Breaker (Public Demo)
MethodEndpointDescriptionPOST/api/v1/circuit-breaker/paymentTest payment with CBPOST/api/v1/circuit-breaker/loan/{loanId}Test loan with CBPOST/api/v1/circuit-breaker/transferTest transfer with CBPOST/api/v1/circuit-breaker/gateway/downSimulate gateway DOWNPOST/api/v1/circuit-breaker/gateway/upSimulate gateway UPGET/api/v1/circuit-breaker/statusView CB states
Resilience (Retry + Bulkhead + CB combined)
MethodEndpointDescriptionPOST/api/v1/resilience/paymentPayment with all 3 patternsPOST/api/v1/resilience/transferTransfer with all 3 patternsPOST/api/v1/resilience/loan/{loanId}Loan with bulkhead + CBGET/api/v1/resilience/statusAll resilience stats
Event Sourcing
MethodEndpointDescriptionPOST/api/v1/events/publishPublish eventGET/api/v1/events/account/{accountId}Full event historyGET/api/v1/events/account/{accountId}/rebuildRebuild state from eventsGET/api/v1/events/account/{accountId}/at/{seq}State at specific pointGET/api/v1/events/account/{accountId}/type/{type}Events by type
Cache
MethodEndpointDescriptionGET/api/v1/cache/statsCache hit/missDELETE/api/v1/cache/clearClear all caches
Audit Logs (ADMIN only)
MethodEndpointDescriptionGET/api/v1/auditRecent 50 logsGET/api/v1/audit/user/{username}Logs by userGET/api/v1/audit/action/{action}Logs by actionGET/api/v1/audit/entity/{type}Logs by entityGET/api/v1/audit/date?start=2025-01-01&end=2025-01-31Logs by date
Rate Limiting
MethodEndpointDescriptionGET/api/v1/rate-limit/infoView policy

All responses include X-Rate-Limit-Remaining header. Exceeding 20 req/min returns 429.


🔮 GraphQL API
Open http://localhost:8080/graphiql for the interactive playground.
Why GraphQL alongside REST?
ScenarioRESTGraphQLGet account + transactions + loans3 API calls1 queryGet only account name & balanceReturns all fieldsReturns only requestedAdd new field to responseNew API versionJust add to query
Example Queries
Fetch account with nested transactions & loans (1 query = 3 REST calls):
graphql{
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
Create account:
graphqlmutation {
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
Transfer money:
graphqlmutation {
transfer(input: {
fromAccountId: 1
toAccountId: 2
amount: 500
}) {
message
amount
}
}

🔄 Enterprise Patterns Implemented
1. Circuit Breaker (Resilience4j)
   🟢 CLOSED    → Normal, requests pass through
   🔴 OPEN      → Too many failures, fallback returned instantly
   🟡 HALF-OPEN → Testing if service recovered
2. Retry with Exponential Backoff
   Attempt 1 → Fails → Wait 1s
   Attempt 2 → Fails → Wait 2s
   Attempt 3 → Fails → Fallback response
3. Bulkhead Isolation
   Payment service  → Max 10 concurrent requests
   Loan service     → Max 5 concurrent requests
   Transfer service → Max 15 concurrent requests
4. Event Sourcing
   Event 1: ACCOUNT_CREATED  → balance: 0
   Event 2: DEPOSITED         → +5000, balance: 5000
   Event 3: WITHDRAWN         → -500,  balance: 4500
   Event 4: TRANSFERRED_OUT   → -1000, balance: 3500
   → Can rebuild state at ANY point in time
5. Caching (Caffeine)
   1st GET /accounts/1 → Database query → Stored in cache
   2nd GET /accounts/1 → Returned from cache instantly (no DB)
   On update           → Cache cleared automatically
6. API Versioning
   GET /api/v1/accounts/1 → Raw entity response
   GET /api/v2/accounts/1 → Enhanced DTO with formatted balance & status

🛡️ Global Exception Handling
All errors return a consistent JSON format:
json{
"status": 404,
"error": "Not Found",
"message": "Account not found with ID: 99",
"path": "/api/v1/accounts/99",
"timestamp": "2026-02-22T20:15:00"
}
StatusWhen400Validation errors, bad input401Invalid/expired token403No permission (wrong role)404Resource not found405Wrong HTTP method409Duplicate resource422Insufficient balance429Rate limit exceeded503External service unavailable500Unexpected server error


## 📁 Project Structure

```
banking-app/
├── pom.xml
├── bankingdb.db                             
├── src/main/java/com/banking/app/
│   ├── BankingAppApplication.java
│   ├── config/
│   │   ├── CacheConfig.java
|   |   ├── SecurityConfig.java               ← JWT + CORS + role-based access
│   │   ├── SwaggerConfig.java                ← OpenAPI documentation
│   │   ├── AuditInterceptor.java             ← Auto-logs every API request
│   │   ├── WebConfig.java                    ← Registers interceptors
│   │   └── RateLimitFilter.java              ← 20 req/min per IP
│   ├── security/
│   │   ├── JwtUtil.java
│   │   ├── JwtAuthFilter.java
│   │   └── CustomUserDetailsService.java
│   ├── graphql/
│   │   ├── QueryResolver.java
│   │   └── MutationResolver.java
│   ├── controller/
│   │   ├── AccountController.java
│   │   ├── AccountControllerV2.java
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
│   │   ├── ApiError.java
│   │   ├── AccountResponseV2.java
│   │   └── TransferRequestV2.java
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── BadRequestException.java
│   │   ├── DuplicateResourceException.java
│   │   ├── GlobalExceptionHandler.java
│   │   ├── InsufficientBalanceException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── ServiceUnavailableException.java
│   │   └── UnauthorizedException.java
│   ├── model/
│   │   ├── Account.java
│   │   ├── AccountEvent.java
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
│       ├── AccountService.java
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
        └── schema.graphqls
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