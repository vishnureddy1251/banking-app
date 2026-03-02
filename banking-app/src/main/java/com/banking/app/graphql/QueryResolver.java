package com.banking.app.graphql;

import com.banking.app.model.*;
import com.banking.app.repository.*;
import com.banking.app.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryResolver {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final LoanService loanService;
    private final LoanRepository loanRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final BillPaymentService billPaymentService;
    private final BillPaymentRepository billPaymentRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;


    @QueryMapping
    public List<Account> allAccounts() {
        log.info("GraphQL: Fetching all accounts");
        return accountService.getAllAccounts();
    }

    @QueryMapping
    public Account accountById(@Argument Long id) {
        log.info("GraphQL: Fetching account {}", id);
        return accountService.getAccountById(id);
    }

    @QueryMapping
    public Account accountByNumber(@Argument String accountNumber) {
        log.info("GraphQL: Fetching account by number {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    @SchemaMapping(typeName = "Account", field = "transactions")
    public List<Transaction> getTransactionsForAccount(Account account) {
        log.info("GraphQL: Loading transactions for account {}", account.getId());
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(account.getId());
    }

    @SchemaMapping(typeName = "Account", field = "loans")
    public List<Loan> getLoansForAccount(Account account) {
        log.info("GraphQL: Loading loans for account {}", account.getId());
        return loanRepository.findByAccountId(account.getId());
    }

    @QueryMapping
    public List<Customer> allCustomers() {
        log.info("GraphQL: Fetching all customers");
        return customerRepository.findAll();
    }

    @QueryMapping
    public Customer customerById(@Argument Long id) {
        log.info("GraphQL: Fetching customer {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    @QueryMapping
    public List<Customer> searchCustomers(@Argument String name) {
        log.info("GraphQL: Searching customers by name: {}", name);
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @QueryMapping
    public List<Transaction> transactionsByAccount(@Argument Long accountId) {
        log.info("GraphQL: Fetching transactions for account {}", accountId);
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
    }

    @QueryMapping
    public List<Transaction> transactionsByType(@Argument Long accountId, @Argument String type) {
        log.info("GraphQL: Fetching {} transactions for account {}", type, accountId);
        return transactionRepository.findByAccountIdAndTransactionType(accountId, type);
    }

    @QueryMapping
    public List<Loan> allLoans() {
        log.info("GraphQL: Fetching all loans");
        return loanRepository.findAll();
    }

    @QueryMapping
    public Loan loanById(@Argument Long id) {
        log.info("GraphQL: Fetching loan {}", id);
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found: " + id));
    }

    @QueryMapping
    public List<Loan> loansByAccount(@Argument Long accountId) {
        log.info("GraphQL: Fetching loans for account {}", accountId);
        return loanRepository.findByAccountId(accountId);
    }

    @QueryMapping
    public List<Loan> loansByStatus(@Argument String status) {
        log.info("GraphQL: Fetching loans by status {}", status);
        return loanRepository.findByStatus(status);
    }

    @QueryMapping
    public List<Notification> notificationsByAccount(@Argument Long accountId) {
        log.info("GraphQL: Fetching notifications for account {}", accountId);
        return notificationRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    @QueryMapping
    public List<Notification> unreadNotifications(@Argument Long accountId) {
        log.info("GraphQL: Fetching unread notifications for account {}", accountId);
        return notificationRepository.findByAccountIdAndIsReadFalseOrderByCreatedAtDesc(accountId);
    }

    @QueryMapping
    public long unreadCount(@Argument Long accountId) {
        return notificationRepository.countByAccountIdAndIsReadFalse(accountId);
    }

    @QueryMapping
    public List<BillPayment> billsByAccount(@Argument Long accountId) {
        log.info("GraphQL: Fetching bills for account {}", accountId);
        return billPaymentRepository.findByAccountIdOrderByPaymentDateDesc(accountId);
    }
}
