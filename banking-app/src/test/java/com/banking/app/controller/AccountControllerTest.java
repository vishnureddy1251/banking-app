package com.banking.app.controller;

import com.banking.app.model.Account;
import com.banking.app.security.JwtUtil;
import com.banking.app.service.AccountService;
import com.banking.app.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JwtUtil.class)
@DisplayName("AccountController")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AuditLogService auditLogService;

    @Test
    @DisplayName("POST /api/v1/accounts returns 201")
    void createAccount() throws Exception {
        Account body = Account.builder()
                .accountName("Alice")
                .accountType("SAVINGS")
                .balance(new BigDecimal("100.00"))
                .build();
        Account saved = Account.builder()
                .id(1L)
                .accountName("Alice")
                .accountNumber("ACC12345678")
                .accountType("SAVINGS")
                .balance(new BigDecimal("100.00"))
                .build();
        when(accountService.createAccount(any(Account.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountName").value("Alice"));

        verify(accountService).createAccount(any(Account.class));
    }

    @Test
    @DisplayName("GET /api/v1/accounts/{id} returns account")
    void getAccount() throws Exception {
        Account a = Account.builder().id(2L).accountName("Bob").accountNumber("ACC1")
                .accountType("CHECKING").balance(BigDecimal.TEN).build();
        when(accountService.getAccountById(2L)).thenReturn(a);

        mockMvc.perform(get("/api/v1/accounts/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName").value("Bob"));
    }

    @Test
    @DisplayName("GET /api/v1/accounts returns list")
    void getAll() throws Exception {
        when(accountService.getAllAccounts()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("PUT deposit and withdraw delegate to service")
    void depositWithdraw() throws Exception {
        Account after = Account.builder().id(1L).accountName("A").accountNumber("ACC1")
                .accountType("SAVINGS").balance(new BigDecimal("150")).build();
        when(accountService.deposit(eq(1L), eq(new BigDecimal("50")))).thenReturn(after);
        when(accountService.withdraw(eq(1L), eq(new BigDecimal("25")))).thenReturn(after);

        mockMvc.perform(put("/api/v1/accounts/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150));

        mockMvc.perform(put("/api/v1/accounts/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":25}"))
                .andExpect(status().isOk());

        verify(accountService).deposit(1L, new BigDecimal("50"));
        verify(accountService).withdraw(1L, new BigDecimal("25"));
    }

    @Test
    @DisplayName("POST /api/v1/accounts/transfer returns message map")
    void transfer() throws Exception {
        when(accountService.transfer(1L, 2L, new BigDecimal("10.5")))
                .thenReturn("Successfully transferred");

        mockMvc.perform(post("/api/v1/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromAccountId":1,"toAccountId":2,"amount":10.5}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully transferred"));
    }

    @Test
    @DisplayName("DELETE /api/v1/accounts/{id} returns success message")
    void deleteAccount() throws Exception {
        mockMvc.perform(delete("/api/v1/accounts/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account deleted successfully"));

        verify(accountService).deleteAccount(5L);
    }
}
