package com.banking.app.controller;

import com.banking.app.model.Account;
import com.banking.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public ResponseEntity<Account> createAccount(@Valid @ResponseBody Account account){
        Account created = AccountService.createAccount(account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
