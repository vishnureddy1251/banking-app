package com.banking.app;

import com.banking.app.model.Transaction;
import com.banking.app.repository.TransactionRepository;
import com.banking.app.service.TransactionService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Nested
    @DisplayName("logTransaction")
    class LogTransactionTests {

        @Test
        @DisplayName("persists transaction with all fields and returns saved entity")
        void persistsAndReturnsSaved() {
            Transaction saved = Transaction.builder()
                    .id(10L)
                    .accountId(1L)
                    .transactionType("DEPOSIT")
                    .amount(new BigDecimal("100.00"))
                    .balanceAfter(new BigDecimal("1100.00"))
                    .relatedAccountId(null)
                    .description("Cash deposit")
                    .build();
            when(transactionRepository.save(any(Transaction.class)))
                    .thenReturn(saved);

            Transaction result = transactionService.logTransaction(
                    1L, "DEPOSIT", new BigDecimal("100.00"),
                    new BigDecimal("1100.00"), null, "Cash deposit");

            assertThat(result).isSameAs(saved);

            ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
            verify(transactionRepository).save(captor.capture());
            Transaction captured = captor.getValue();
            assertThat(captured.getAccountId()).isEqualTo(1L);
            assertThat(captured.getTransactionType()).isEqualTo("DEPOSIT");
            assertThat(captured.getAmount()).isEqualByComparingTo("100.00");
            assertThat(captured.getBalanceAfter()).isEqualByComparingTo("1100.00");
            assertThat(captured.getRelatedAccountId()).isNull();
            assertThat(captured.getDescription()).isEqualTo("Cash deposit");
        }
    }

    @Nested
    @DisplayName("queries")
    class QueryTests {

        @Test
        @DisplayName("getTransactionsByAccountId delegates to repository")
        void getByAccountId() {
            List<Transaction> list = List.of(Transaction.builder().id(1L).accountId(5L).build());
            when(transactionRepository.findByAccountIdOrderByTransactionDateDesc(5L)).thenReturn(list);

            assertThat(transactionService.getTransactionsByAccountId(5L)).isSameAs(list);
            verify(transactionRepository).findByAccountIdOrderByTransactionDateDesc(5L);
        }

        @Test
        @DisplayName("getTransactionsByType delegates to repository")
        void getByType() {
            List<Transaction> list = List.of();
            when(transactionRepository.findByAccountIdAndTransactionType(3L, "WITHDRAWAL"))
                    .thenReturn(list);

            assertThat(transactionService.getTransactionsByType(3L, "WITHDRAWAL")).isSameAs(list);
            verify(transactionRepository).findByAccountIdAndTransactionType(eq(3L), eq("WITHDRAWAL"));
        }
    }
}
