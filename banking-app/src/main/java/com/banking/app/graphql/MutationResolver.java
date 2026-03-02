package com.banking.app.graphql;

import com.banking.app.model.Account;
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
}
