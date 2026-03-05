package com.banking.app;

import com.banking.app.model.Account;
import com.banking.app.repository.AccountRepository;
import com.banking.app.service.AccountService;
import com.banking.app.service.TransactionService;
import com.banking.app.service.WebSocketNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Tests")
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WebSocketNotificationService wsNotificationService;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setAccountName("Arjun Don");
        testAccount.setAccountNumber("ACC12345678");
        testAccount.setAccountType("SAVINGS");
        testAccount.setBalance(new BigDecimal("5000.00"));
    }
}
