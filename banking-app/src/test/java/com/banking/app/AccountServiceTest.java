package com.banking.app;

import com.banking.app.model.Account;
import com.banking.app.repository.AccountRepository;
import com.banking.app.service.AccountService;
import com.banking.app.service.TransactionService;
import com.banking.app.service.WebSocketNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

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

    @Nested
    @DisplayName("Create Account")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully with balance")
        void shouldCreateAccountWithBalance() {
            Account newAccount = new Account();
            newAccount.setAccountName("New User");
            newAccount.setAccountType("SAVINGS");
            newAccount.setBalance(new BigDecimal("1000.00"));

            when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

            Account result = accountService.createAccount(newAccount);

            assertThat(result).isNotNull();
            assertThat(result.getAccountName()).isEqualTo("New User");
            assertThat(result.getAccountType()).isEqualTo("SAVINGS");
            assertThat(result.getAccountNumber()).startsWith("ACC");
            verify(accountRepository, times(1)).save(any(Account.class));
        }
}
