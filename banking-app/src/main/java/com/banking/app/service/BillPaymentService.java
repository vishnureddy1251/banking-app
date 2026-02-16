package com.banking.app.service;

import com.banking.app.exception.AccountNotFoundException;
import com.banking.app.model.BillPayment;
import com.banking.app.repository.BillPaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final AccountService accountService;

    @Transactional
    public BillPayment payBill(BillPayment bill) {

        accountService.getAccountById(bill.getAccountId());

        String refNo = "BILL" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        bill.setReferenceNumber(refNo);
        bill.setStatus("PENDING");

        try {
            accountService.withdraw(bill.getAccountId(), bill.getAmount());
            bill.setStatus("COMPLETED");
            log.info("Bill payment successful: {} - ${} - Ref: {}", bill.getBillType(), bill.getAmount(), refNo);
        } catch (Exception e) {
            bill.setStatus("FAILED");
            log.error("Bill payment failed: {} - {}", refNo, e.getMessage());
        }

        return billPaymentRepository.save(bill);
    }

    public List<BillPayment> getPaymentsByAccountId(Long accountId) {
        return billPaymentRepository.findByAccountIdOrderByPaymentDateDesc(accountId);
    }

    public List<BillPayment> getPaymentsByType(Long accountId, String billType) {
        return billPaymentRepository.findByAccountIdAndBillType(accountId, billType.toUpperCase());
    }

    public BillPayment getPaymentByReference(String referenceNumber) {
        return billPaymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new AccountNotFoundException("Payment not found with ref: " + referenceNumber));
    }
}
