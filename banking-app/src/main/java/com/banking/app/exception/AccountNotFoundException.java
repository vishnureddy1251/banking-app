package com.banking.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public interface AccountNotFoundException extends RuntimeException {
    public class AccountNotFoundException (String message){
        super(message);
    }
}
