package com.banking.app.controller;

import com.banking.app.model.BillPayment;
import com.banking.app.security.JwtUtil;
import com.banking.app.service.AuditLogService;
import com.banking.app.service.BillPaymentService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BillPaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JwtUtil.class)
@DisplayName("BillPaymentController")
class BillPaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BillPaymentService billPaymentService;

    @MockitoBean
    private AuditLogService auditLogService;

    @Test
    @DisplayName("POST /api/v1/bills/pay returns 201 with saved payment")
    void payBill() throws Exception {
        BillPayment request = BillPayment.builder()
                .accountId(1L)
                .billType("UTILITIES")
                .providerName("Co")
                .consumerNumber("C1")
                .amount(new BigDecimal("25.00"))
                .build();
        BillPayment completed = BillPayment.builder()
                .id(10L)
                .accountId(1L)
                .billType("UTILITIES")
                .providerName("Co")
                .consumerNumber("C1")
                .amount(new BigDecimal("25.00"))
                .status("COMPLETED")
                .referenceNumber("BILLABCDEF01")
                .build();
        when(billPaymentService.payBill(any(BillPayment.class)))
                .thenReturn(completed);

        mockMvc.perform(post("/api/v1/bills/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.referenceNumber").value("BILLABCDEF01"));
    }

    @Test
    @DisplayName("GET /api/v1/bills/account/{id} without type lists all payments")
    void allForAccount() throws Exception {
        when(billPaymentService.getPaymentsByAccountId(7L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/bills/account/7"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(billPaymentService).getPaymentsByAccountId(7L);
    }

    @Test
    @DisplayName("GET /api/v1/bills/account/{id} with type filters by bill type")
    void byType() throws Exception {
        when(billPaymentService.getPaymentsByType(7L, "electric")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/bills/account/7").param("type", "electric"))
                .andExpect(status().isOk());

        verify(billPaymentService).getPaymentsByType(7L, "electric");
    }

    @Test
    @DisplayName("GET with blank type parameter lists all (empty treated as absent)")
    void blankTypeUsesAll() throws Exception {
        when(billPaymentService.getPaymentsByAccountId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/bills/account/1").param("type", ""))
                .andExpect(status().isOk());

        verify(billPaymentService).getPaymentsByAccountId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/bills/track/{ref} returns payment")
    void trackPayment() throws Exception {
        BillPayment p = BillPayment.builder()
                .referenceNumber("BILL12345678")
                .accountId(1L)
                .billType("WATER")
                .providerName("W")
                .consumerNumber("1")
                .amount(BigDecimal.ONE)
                .build();
        when(billPaymentService.getPaymentByReference("BILL12345678")).thenReturn(p);

        mockMvc.perform(get("/api/v1/bills/track/BILL12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").value("BILL12345678"));
    }
}
