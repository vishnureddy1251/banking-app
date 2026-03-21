package com.banking.app;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.exception.InsufficientBalanceException;
import com.banking.app.model.Account;
import com.banking.app.model.BillPayment;
import com.banking.app.repository.BillPaymentRepository;
import com.banking.app.service.AccountService;
import com.banking.app.service.BillPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillPaymentService Tests")
class BillPaymentServiceTest {

    @Mock
    private BillPaymentRepository billPaymentRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private BillPaymentService billPaymentService;

    private BillPayment bill;
    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1L);
        bill = BillPayment.builder()
                .accountId(1L)
                .billType("UTILITIES")
                .providerName("PowerCo")
                .consumerNumber("C-1")
                .amount(new BigDecimal("50.00"))
                .build();
    }

    @Nested
    @DisplayName("payBill")
    class PayBillTests {

        @Test
        @DisplayName("completes payment and saves when withdraw succeeds")
        void success() {
            when(accountService.getAccountById(1L)).thenReturn(account);
            when(accountService.withdraw(eq(1L), eq(new BigDecimal("50.00")))).thenReturn(account);
            when(billPaymentRepository.save(any(BillPayment.class))).thenAnswer(i -> i.getArgument(0));

            BillPayment result = billPaymentService.payBill(bill);

            assertThat(result.getStatus()).isEqualTo("COMPLETED");
            assertThat(result.getReferenceNumber()).startsWith("BILL");
            assertThat(result.getReferenceNumber()).hasSize(4 + 8);

            ArgumentCaptor<BillPayment> captor = ArgumentCaptor.forClass(BillPayment.class);
            verify(billPaymentRepository).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("marks FAILED and still saves when withdraw throws")
        void withdrawFails() {
            when(accountService.getAccountById(1L)).thenReturn(account);
            when(accountService.withdraw(eq(1L), any())).thenThrow(new InsufficientBalanceException("Insufficient"));
            when(billPaymentRepository.save(any(BillPayment.class))).thenAnswer(i -> i.getArgument(0));

            BillPayment result = billPaymentService.payBill(bill);

            assertThat(result.getStatus()).isEqualTo("FAILED");
            verify(billPaymentRepository).save(bill);
        }

        @Test
        @DisplayName("does not save when account lookup fails")
        void accountMissing() {
            when(accountService.getAccountById(1L)).thenThrow(new AccountNotFoundException("not found"));

            assertThatThrownBy(() -> billPaymentService.payBill(bill))
                    .isInstanceOf(AccountNotFoundException.class);

            verify(billPaymentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("queries")
    class QueryTests {

        @Test
        @DisplayName("getPaymentsByAccountId delegates to repository")
        void byAccount() {
            List<BillPayment> list = List.of(bill);
            when(billPaymentRepository.findByAccountIdOrderByPaymentDateDesc(2L)).thenReturn(list);

            assertThat(billPaymentService.getPaymentsByAccountId(2L)).isSameAs(list);
        }

        @Test
        @DisplayName("getPaymentsByType uppercases bill type for repository")
        void byTypeUppercase() {
            when(billPaymentRepository.findByAccountIdAndBillType(1L, "UTILITIES")).thenReturn(List.of());

            billPaymentService.getPaymentsByType(1L, "utilities");

            verify(billPaymentRepository).findByAccountIdAndBillType(1L, "UTILITIES");
        }

        @Test
        @DisplayName("getPaymentByReference returns entity when found")
        void byRefFound() {
            when(billPaymentRepository.findByReferenceNumber("BILL12345678")).thenReturn(Optional.of(bill));

            assertThat(billPaymentService.getPaymentByReference("BILL12345678")).isSameAs(bill);
        }

        @Test
        @DisplayName("getPaymentByReference throws when not found")
        void byRefMissing() {
            when(billPaymentRepository.findByReferenceNumber("UNKNOWN")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> billPaymentService.getPaymentByReference("UNKNOWN"))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining("UNKNOWN");
        }
    }
}
