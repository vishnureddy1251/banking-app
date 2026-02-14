package com.banking.app.repository;

import com.banking.app.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByAccountId(Long accountId);

    List<Loan> findByStatus(String status);

    List<Loan> findByAccountIdAndStatus(Long accountId, String status);

}
