package com.banking.app.service;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.model.Customer;
import com.banking.app.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Customer getCustomerById(Long id){
        return customerRepository.findById(id)
                .orElseThrow(()-> new AccountNotFoundException("Customer not found with ID:" + id));
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public List<Customer> searchCustomers(String name){
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer updated){
        Customer existing = getCustomerById(id);

        if (updated.getFirstName() != null) existing.setFirstName(updated.getFirstName());
        if (updated.getLastName() != null) existing.setLastName(updated.getLastName());
        if (updated.getPhone() != null) existing.setPhone(updated.getPhone());
        if (updated.getAddress() != null) existing.setAddress(updated.getAddress());
        if (updated.getCity() != null) existing.setCity(updated.getCity());
        if (updated.getState() != null) existing.setState(updated.getState());
        if (updated.getZipCode() != null) existing.setZipCode(updated.getZipCode());
        if (updated.getDateOfBirth() != null) existing.setDateOfBirth(updated.getDateOfBirth());

        Customer saved = customerRepository.save(existing);
        log.info("Customer updated: ID {}", id);
        return saved;
    }

    @Transactional
    public void deleteCustomer(Long id){
    Customer customer = getCustomerById(id);
    customerRepository.delete(customer);
    log.info("Customer deleted: {} {}", customer.getFirstName(), customer.getLastName());
    }
}
