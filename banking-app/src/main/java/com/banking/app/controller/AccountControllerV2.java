package com.banking.app.controller;

import com.banking.app.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.NumberFormat;
import java.util.Locale;

@RestController
@RequestMapping("/api/v2/accounts")
@RequiredArgsConstructor
public class AccountControllerV2 {

    private final AccountService accountService;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
}
