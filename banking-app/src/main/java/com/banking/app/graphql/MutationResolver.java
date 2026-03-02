package com.banking.app.graphql;

import com.banking.app.model.*;
import com.banking.app.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MutationResolver {

    private final AccountService accountService;
    private final CustomerService customerService;
    private final LoanService loanService;
    private final BillPaymentService billPaymentService;
    private final NotificationService notificationService;

    @MutationMapping
    public Account createAccount(@Argument Map<String, Object> input) {
        log.info("GraphQL: Creating account for {}", input.get("accountName"));

        Account account = new Account();
        account.setAccountName((String) input.get("accountName"));
        account.setAccountType((String) input.get("accountType"));
        if (input.get("balance") != null) {
            account.setBalance(new BigDecimal(input.get("balance").toString()));
        }

        return accountService.createAccount(account);
    }

    @MutationMapping
    public Account deposit(@Argument Long accountId, @Argument Double amount) {
        log.info("GraphQL: Depositing {} to account {}", amount, accountId);
        return accountService.deposit(accountId, BigDecimal.valueOf(amount));
    }

    @MutationMapping
    public Account withdraw(@Argument Long accountId, @Argument Double amount) {
        log.info("GraphQL: Withdrawing {} from account {}", amount, accountId);
        return accountService.withdraw(accountId, BigDecimal.valueOf(amount));
    }

    @MutationMapping
    public Map<String, Object> transfer(@Argument Map<String, Object> input) {
        Long fromId = Long.valueOf(input.get("fromAccountId").toString());
        Long toId = Long.valueOf(input.get("toAccountId").toString());
        BigDecimal amount = new BigDecimal(input.get("amount").toString());

        log.info("GraphQL: Transferring {} from {} to {}", amount, fromId, toId);
        String result = accountService.transfer(fromId, toId, amount);

        return Map.of(
                "message", result,
                "fromAccountId", fromId,
                "toAccountId", toId,
                "amount", amount.doubleValue()
        );
    }

    @MutationMapping
    public String deleteAccount(@Argument Long id) {
        log.info("GraphQL: Deleting account {}", id);
        accountService.deleteAccount(id);
        return "Account " + id + " deleted successfully";
    }

    @MutationMapping
    public Customer createCustomer(@Argument Map<String, Object> input) {
        log.info("GraphQL: Creating customer {}", input.get("firstName"));

        Customer customer = new Customer();
        customer.setFirstName((String) input.get("firstName"));
        customer.setLastName((String) input.get("lastName"));
        customer.setEmail((String) input.get("email"));
        customer.setPhone((String) input.get("phone"));
        customer.setAddress((String) input.get("address"));
        customer.setCity((String) input.get("city"));
        customer.setState((String) input.get("state"));
        customer.setZipCode((String) input.get("zipCode"));

        return customerService.createCustomer(customer);
    }

    @MutationMapping
    public Loan applyLoan(@Argument Map<String, Object> input) {
        Long accountId = Long.valueOf(input.get("accountId").toString());
        log.info("GraphQL: Applying loan for account {}", accountId);

        Loan loan = new Loan();
        loan.setAccountId(accountId);
        loan.setLoanType((String) input.get("loanType"));
        loan.setLoanAmount(new BigDecimal(input.get("loanAmount").toString()));
        loan.setInterestRate(new BigDecimal(input.get("interestRate").toString()));
        loan.setTenureMonths(Integer.parseInt(input.get("tenureMonths").toString()));

        return loanService.applyForLoan(loan);
    }

    @MutationMapping
    public Loan approveLoan(@Argument Long id) {
        log.info("GraphQL: Approving loan {}", id);
        return loanService.approveLoan(id);
    }

    @MutationMapping
    public Loan rejectLoan(@Argument Long id) {
        log.info("GraphQL: Rejecting loan {}", id);
        return loanService.rejectLoan(id);
    }

    @MutationMapping
    public Loan repayLoan(@Argument Long id, @Argument Double amount) {
        log.info("GraphQL: Repaying {} on loan {}", amount, id);
        return loanService.repayLoan(id, BigDecimal.valueOf(amount));
    }

    @MutationMapping
    public BillPayment payBill(@Argument Map<String, Object> input) {
        Long accountId = Long.valueOf(input.get("accountId").toString());
        log.info("GraphQL: Paying bill for account {}", accountId);

        BillPayment bill = new BillPayment();
        bill.setAccountId(accountId);
        bill.setBillType((String) input.get("billType"));
        bill.setProviderName((String) input.get("providerName"));
        bill.setConsumerNumber((String) input.get("consumerNumber"));
        bill.setAmount(new BigDecimal(input.get("amount").toString()));

        return billPaymentService.payBill(bill);
    }

    @MutationMapping
    public Notification markAsRead(@Argument Long id) {
        log.info("GraphQL: Marking notification {} as read", id);
        return notificationService.markAsRead(id);
    }

    @MutationMapping
    public String markAllAsRead(@Argument Long accountId) {
        log.info("GraphQL: Marking all notifications as read for account {}", accountId);
        notificationService.markAllAsRead(accountId);
        return "All notifications marked as read for account " + accountId;
    }
}
