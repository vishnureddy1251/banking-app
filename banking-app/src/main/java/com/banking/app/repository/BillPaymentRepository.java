package com.banking.app.repository;

import com.banking.app.model.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {

    List<BillPayment> findByAccountIdOrderByPaymentDateDesc(Long accountId);

    List<BillPayment> findByAccountIdAndBillType(Long accountId, String billType);

    Optional<BillPayment> findByReferenceNumber(String referenceNumber);
}
