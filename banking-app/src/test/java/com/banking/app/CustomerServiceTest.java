package com.banking.app;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.model.Customer;
import com.banking.app.repository.CustomerRepository;
import com.banking.app.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .phone("555-0100")
                .build();
    }

    @Nested
    @DisplayName("createCustomer")
    class CreateTests {

        @Test
        @DisplayName("saves when email is new")
        void savesNewEmail() {
            when(customerRepository.existsByEmail("jane@example.com")).thenReturn(false);
            when(customerRepository.save(customer)).thenReturn(customer);

            assertThat(customerService.createCustomer(customer)).isSameAs(customer);
            verify(customerRepository).save(customer);
        }

        @Test
        @DisplayName("throws when email already registered")
        void duplicateEmail() {
            when(customerRepository.existsByEmail("jane@example.com")).thenReturn(true);

            assertThatThrownBy(() -> customerService.createCustomer(customer))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email already registered");

            verify(customerRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getCustomerById")
    class GetByIdTests {

        @Test
        @DisplayName("returns customer when present")
        void found() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

            assertThat(customerService.getCustomerById(1L)).isSameAs(customer);
        }

        @Test
        @DisplayName("throws AccountNotFoundException when missing")
        void notFound() {
            when(customerRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.getCustomerById(99L))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("list and search")
    class ListSearchTests {

        @Test
        @DisplayName("getAllCustomers returns repository list")
        void getAll() {
            List<Customer> all = List.of(customer);
            when(customerRepository.findAll()).thenReturn(all);

            assertThat(customerService.getAllCustomers()).isSameAs(all);
        }

        @Test
        @DisplayName("searchCustomers passes same term for first and last name")
        void search() {
            List<Customer> results = List.of();
            when(customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("jan", "jan"))
                    .thenReturn(results);

            assertThat(customerService.searchCustomers("jan")).isSameAs(results);
        }
    }

    @Nested
    @DisplayName("updateCustomer")
    class UpdateTests {

        @Test
        @DisplayName("applies non-null fields and saves")
        void partialUpdate() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            Customer patch = new Customer();
            patch.setFirstName("Janet");
            patch.setCity("Boston");
            patch.setDateOfBirth(LocalDate.of(1990, 1, 2));
            when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

            Customer updated = customerService.updateCustomer(1L, patch);

            assertThat(updated.getFirstName()).isEqualTo("Janet");
            assertThat(updated.getLastName()).isEqualTo("Doe");
            assertThat(updated.getCity()).isEqualTo("Boston");
            assertThat(updated.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 2));
            verify(customerRepository).save(customer);
        }
    }

    @Nested
    @DisplayName("deleteCustomer")
    class DeleteTests {

        @Test
        @DisplayName("deletes when customer exists")
        void deletes() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

            customerService.deleteCustomer(1L);

            verify(customerRepository).delete(customer);
        }
    }
}
