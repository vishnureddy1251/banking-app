# 🏦 Banking Application - Spring Boot

A simple banking REST API built with Spring Boot for learning purposes.

## 🏗️ Architecture (Layered Pattern)

```
Client (Postman/Browser)
    ↓
Controller Layer  → Handles HTTP requests/responses
    ↓
Service Layer     → Business logic (deposit, withdraw, transfer)
    ↓
Repository Layer  → Database operations (CRUD)
    ↓
Database (H2)     → In-memory database
```

## 🛠️ Tech Stack

| Technology | Purpose |
|-----------|---------|
| Java 17 | Programming language |
| Spring Boot 3.5.10 | Application framework |
| Spring Data JPA | Database access (ORM) |
| H2 Database | In-memory database |
| Lombok | Reduces boilerplate code |
| Maven | Build & dependency management |

## 🚀 How to Run

```bash
# Clone the repo
git clone https://github.com/YOUR_USERNAME/banking-app.git
cd banking-app

# Run the application
./mvnw spring-boot:run

# App starts at: http://localhost:8080
# H2 Console at: http://localhost:8080/h2-console
```

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Create new account |
| GET | `/api/v1/accounts` | Get all accounts |
| GET | `/api/v1/accounts/{id}` | Get account by ID |
| PUT | `/api/v1/accounts/{id}/deposit` | Deposit money |
| PUT | `/api/v1/accounts/{id}/withdraw` | Withdraw money |
| POST | `/api/v1/accounts/transfer` | Transfer between accounts |
| DELETE | `/api/v1/accounts/{id}` | Delete account |

## 🧪 Test with cURL

### 1. Create Account
```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountHolderName": "John Doe",
    "accountType": "SAVINGS",
    "balance": 1000.00
  }'
```

### 2. Deposit Money
```bash
curl -X PUT http://localhost:8080/api/v1/accounts/1/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.00}'
```

### 3. Withdraw Money
```bash
curl -X PUT http://localhost:8080/api/v1/accounts/1/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount": 200.00}'
```

## 📁 Project Structure

```
banking-app/
├── pom.xml
├── src/main/java/com/banking/app/
│   ├── BankingAppApplication.java     # Main class (entry point)
│   ├── model/
│   │   └── Account.java              # Entity (DB table mapping)
│   ├── repository/
│   │   └── AccountRepository.java    # Database operations
│   ├── service/
│   │   └── AccountService.java       # Business logic
│   ├── controller/
│   │   └── AccountController.java    # REST API endpoints
│   └── exception/
│       ├── AccountNotFoundException.java
│       ├── InsufficientBalanceException.java
│       └── GlobalExceptionHandler.java
└── src/main/resources/
    └── application.properties
```