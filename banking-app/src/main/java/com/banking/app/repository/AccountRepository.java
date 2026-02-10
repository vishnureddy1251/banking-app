package com.banking.app.repository;


import com.banking.app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * REPOSITORY LAYER - Handles all database operations.
 *
 * By extending JpaRepository, we get these methods FOR FREE:
 *   - save(entity)        → INSERT or UPDATE
 *   - findById(id)        → SELECT by primary key
 *   - findAll()           → SELECT all rows
 *   - deleteById(id)      → DELETE by primary key
 *   - count()             → COUNT rows
 *   - existsById(id)      → Check if row exists
 *
 * Spring Data JPA auto-generates the SQL query from the method name.
 */

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {


    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
