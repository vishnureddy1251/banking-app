package com.banking.app.service;

import com.banking.app.model.Customer;
import com.banking.app.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(Customer customer){

        if (customerRepository.existsByEmail(customer.getEmail())){
            throw new IllegalArgumentException("Email already registered: " + customer.getEmail());
        }
        Customer saved = customerRepository.save(customer);
        log.info("Customer created: {} {}", saved.getFirstName(), saved.getLastName());
        return saved;
    }
}
