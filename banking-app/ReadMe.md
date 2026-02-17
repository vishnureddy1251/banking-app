# 🏦 Banking Application - Spring Boot REST API

A comprehensive banking REST API built with **Spring Boot**, featuring account management, transactions, loans, bill payments, customer management, and notifications.

---

## 🏗️ Architecture

```
Client (Postman / Browser / Frontend)
        ↓ HTTP Request
┌──────────────────────────────┐
│      Controller Layer        │  ← Handles HTTP requests/responses
├──────────────────────────────┤
│       Service Layer          │  ← Business logic & validations
├──────────────────────────────┤
│      Repository Layer        │  ← Database operations (JPA)
├──────────────────────────────┤
│     SQLite Database (File)    │  ← Persistent data storage
└──────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Technology       | Purpose                    |
|-----------------|----------------------------|
| Java 17         | Programming language        |
| Spring Boot 3.5 | Application framework       |
| Spring Data JPA | ORM & database access       |
| SQLite          | Persistent file-based database |
| Lombok          | Reduces boilerplate code    |
| Maven           | Build & dependency management |

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
- **Database:** SQLite file stored at `bankingdb.db` in project root
- **View data:** Use [DB Browser for SQLite](https://sqlitebrowser.org/dl/) to open `bankingdb.db`

> Data persists even after restarting the application.

---

## 📡 API Endpoints

### Accounts

| Method | Endpoint                              | Description                |
|--------|---------------------------------------|----------------------------|
| POST   | `/api/v1/accounts`                    | Create new account         |
| GET    | `/api/v1/accounts`                    | Get all accounts           |
| GET    | `/api/v1/accounts/{id}`               | Get account by ID          |
| PUT    | `/api/v1/accounts/{id}/deposit`       | Deposit money              |
| PUT    | `/api/v1/accounts/{id}/withdraw`      | Withdraw money             |
| POST   | `/api/v1/accounts/transfer`           | Transfer between accounts  |
| DELETE | `/api/v1/accounts/{id}`               | Delete account             |

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

| Method | Endpoint                           | Description              |
|--------|------------------------------------|--------------------------|
| POST   | `/api/v1/loans`                    | Apply for a loan         |
| GET    | `/api/v1/loans`                    | Get all loans            |
| GET    | `/api/v1/loans/{id}`               | Get loan by ID           |
| GET    | `/api/v1/loans/account/{accId}`    | Get loans for account    |
| GET    | `/api/v1/loans/status/{status}`    | Filter by status         |
| PUT    | `/api/v1/loans/{id}/approve`       | Approve loan             |
| PUT    | `/api/v1/loans/{id}/reject`        | Reject loan              |
| PUT    | `/api/v1/loans/{id}/repay`         | Make a repayment         |

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

---

## 🧪 Sample API Requests

### Create Account
```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{"accountName": "Arjun Don", "accountType": "SAVINGS", "balance": 1000.00}'
```

### Deposit Money
```bash
curl -X PUT http://localhost:8080/api/v1/accounts/1/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.00}'
```

### Transfer Money
```bash
curl -X POST http://localhost:8080/api/v1/accounts/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromAccountId": 1, "toAccountId": 2, "amount": 100.00}'
```

### Apply for Loan
```bash
curl -X POST http://localhost:8080/api/v1/loans \
  -H "Content-Type: application/json" \
  -d '{"accountId": 1, "loanType": "PERSONAL", "loanAmount": 5000, "interestRate": 5.5, "tenureMonths": 12}'
```

### Pay a Bill
```bash
curl -X POST http://localhost:8080/api/v1/bills/pay \
  -H "Content-Type: application/json" \
  -d '{"accountId": 1, "billType": "ELECTRICITY", "providerName": "Tata Power", "consumerNumber": "CNS123456", "amount": 150.00}'
```

---

## 📁 Project Structure

```
banking-app/
├── pom.xml
├── bankingdb.db                          ← SQLite database file
├── src/main/java/com/banking/app/
│   ├── BankingAppApplication.java
│   ├── controller/
│   │   ├── AccountController.java
│   │   ├── BillPaymentController.java
│   │   ├── CustomerController.java
│   │   ├── LoanController.java
│   │   ├── NotificationController.java
│   │   └── TransactionController.java
│   ├── exception/
│   │   ├── AccountNotFoundException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── InsufficientBalanceException.java
│   ├── model/
│   │   ├── Account.java
│   │   ├── BillPayment.java
│   │   ├── Customer.java
│   │   ├── Loan.java
│   │   ├── Notification.java
│   │   └── Transaction.java
│   ├── repository/
│   │   ├── AccountRepository.java
│   │   ├── BillPaymentRepository.java
│   │   ├── CustomerRepository.java
│   │   ├── LoanRepository.java
│   │   ├── NotificationRepository.java
│   │   └── TransactionRepository.java
│   └── service/
│       ├── AccountService.java
│       ├── BillPaymentService.java
│       ├── CustomerService.java
│       ├── LoanService.java
│       ├── NotificationService.java
│       └── TransactionService.java
└── src/main/resources/
    └── application.properties
```

---

## 🔮 Upcoming Features

- [ ] Beneficiary Management
- [ ] Scheduled Payments (recurring transfers)
- [ ] Branch & ATM Locator
- [ ] Currency Exchange Rates
- [ ] Account Statement (PDF/CSV export)
- [ ] JWT Authentication & Role-Based Access Control
- [ ] Swagger / OpenAPI Documentation
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