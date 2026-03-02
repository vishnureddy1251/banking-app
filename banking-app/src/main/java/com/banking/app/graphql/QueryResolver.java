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
}
