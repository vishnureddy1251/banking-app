package com.banking.app.service;

import com.banking.app.model.BillPayment;
import com.banking.app.repository.BillPaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final AccountService accountService;

    @Transactional
    public BillPayment payBill(BillPayment bill) {
        // Verify account exists
        accountService.getAccountById(bill.getAccountId());

        // Generate reference number
        String refNo = "BILL" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        bill.setReferenceNumber(refNo);
        bill.setStatus("PENDING");

        try {
            // Withdraw amount from account
            accountService.withdraw(bill.getAccountId(), bill.getAmount());
            bill.setStatus("COMPLETED");
            log.info("Bill payment successful: {} - ${} - Ref: {}", bill.getBillType(), bill.getAmount(), refNo);
        } catch (Exception e) {
            bill.setStatus("FAILED");
            log.error("Bill payment failed: {} - {}", refNo, e.getMessage());
        }

        return billPaymentRepository.save(bill);
    }
}
