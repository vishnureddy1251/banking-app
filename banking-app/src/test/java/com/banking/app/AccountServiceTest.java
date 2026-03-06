package com.banking.app;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.model.Account;
import com.banking.app.repository.AccountRepository;
import com.banking.app.service.AccountService;
import com.banking.app.service.TransactionService;
import com.banking.app.service.WebSocketNotificationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        @Test
        @DisplayName("Should create account with zero balance when balance is null")
        void shouldCreateAccountWithZeroBalanceWhenNull() {

            Account newAccount = new Account();
            newAccount.setAccountName("Zero Balance User");
            newAccount.setAccountType("SAVINGS");
            newAccount.setBalance(null);

            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
                Account saved = invocation.getArgument(0);
                return saved;
            });

            Account result = accountService.createAccount(newAccount);

            assertThat(result.getBalance()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should generate unique account number starting with ACC")
        void shouldGenerateUniqueAccountNumber() {

            Account newAccount = new Account();
            newAccount.setAccountName("Test User");
            newAccount.setAccountType("SAVINGS");
            newAccount.setBalance(new BigDecimal("500.00"));

            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Account result = accountService.createAccount(newAccount);

            assertThat(result.getAccountNumber()).isNotNull();
            assertThat(result.getAccountNumber()).startsWith("ACC");
            assertThat(result.getAccountNumber()).hasSize(11);
        }
    }

    @Nested
    @DisplayName("Get Account")
    class GetAccountTests {

        @Test
        @DisplayName("Should return account when ID exists")
        void shouldReturnAccountWhenIdExists() {

            when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

            Account result = accountService.getAccountById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getAccountName()).isEqualTo("Arjun Don");
            assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("5000.00"));
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when ID does not exist")
        void shouldThrowExceptionWhenAccountNotFound() {

            when(accountRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.getAccountById(99L))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("Should return all accounts")
        void shouldReturnAllAccounts() {

            when(accountRepository.findAll()).thenReturn(Arrays.asList(testAccount));

            List<Account> result = accountService.getAllAccounts();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getAccountName()).isEqualTo("Arjun Don");
        }

        @Test
        @DisplayName("Should return empty list when no accounts exist")
        void shouldReturnEmptyListWhenNoAccounts() {

            when(accountRepository.findAll()).thenReturn(List.of());

            List<Account> result = accountService.getAllAccounts();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Deposit")
    class DepositTests {

        @Test
        @DisplayName("Should deposit money successfully")
        void shouldDepositSuccessfully() {

            when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Account result = accountService.deposit(1L, new BigDecimal("2000.00"));

            assertThat(result.getBalance()).isEqualByComparingTo(new BigDecimal("7000.00"));
            verify(transactionService, times(1)).logTransaction(
                    eq(1L), eq("DEPOSIT"), any(BigDecimal.class),
                    any(BigDecimal.class), isNull(), anyString());
        }

        @Test
        @DisplayName("Should throw exception for zero deposit amount")
        void shouldThrowExceptionForZeroDeposit() {

            assertThatThrownBy(() -> accountService.deposit(1L, BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");
        }

        @Test
        @DisplayName("Should throw exception for negative deposit amount")
        void shouldThrowExceptionForNegativeDeposit() {

            assertThatThrownBy(() -> accountService.deposit(1L, new BigDecimal("-500")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("positive");
        }

        @Test
        @DisplayName("Should throw exception when account not found for deposit")
        void shouldThrowExceptionWhenAccountNotFoundForDeposit() {

            when(accountRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> accountService.deposit(99L, new BigDecimal("1000")))
                    .isInstanceOf(AccountNotFoundException.class);
        }
    }
}
