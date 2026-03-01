package com.banking.app.graphql;

import com.banking.app.repository.*;
import com.banking.app.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QueryResolver {

    private final AccountService accountService;
    private final CustomerService customerService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final BillPaymentService billPaymentService;
    private final NotificationService notificationService;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final BillPaymentRepository billPaymentRepository;
}
