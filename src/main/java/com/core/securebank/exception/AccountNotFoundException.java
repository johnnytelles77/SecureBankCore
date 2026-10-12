package com.core.securebank.exception;

public class AccountNotFoundException extends SecureBankException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
