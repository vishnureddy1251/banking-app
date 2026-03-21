package com.banking.app.controller;

import com.banking.app.model.Customer;
import com.banking.app.security.JwtUtil;
import com.banking.app.service.AuditLogService;
import com.banking.app.service.CustomerService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JwtUtil.class)
@DisplayName("CustomerController")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private AuditLogService auditLogService;

    @Test
    void postCreatesCustomer() throws Exception {
        Customer body = Customer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("j@ex.com")
                .build();
        Customer saved = Customer.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("j@ex.com")
                .build();
        when(customerService.createCustomer(any(Customer.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("j@ex.com"));
    }

    @Test
    void getAllReturnsList() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getByIdReturnsCustomer() throws Exception {
        Customer c = Customer.builder().id(3L).firstName("A").lastName("B").email("a@b.c").build();
        when(customerService.getCustomerById(3L)).thenReturn(c);

        mockMvc.perform(get("/api/v1/customers/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("A"));
    }

    @Test
    void searchUsesQueryParam() throws Exception {
        when(customerService.searchCustomers("jan")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/customers/search").param("name", "jan"))
                .andExpect(status().isOk());

        verify(customerService).searchCustomers("jan");
    }

    @Test
    void putUpdatesCustomer() throws Exception {
        Customer patch = new Customer();
        patch.setPhone("555");
        Customer updated = Customer.builder().id(2L).firstName("X").lastName("Y").email("x@y.z").phone("555").build();
        when(customerService.updateCustomer(eq(2L), any(Customer.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/customers/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("555"));
    }

    @Test
    void deleteReturnsMessage() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Customer deleted successfully"));

        verify(customerService).deleteCustomer(9L);
    }
}
