package com.johnny.securebank.exception;

public class AccountNotFoundException extends SecureBankException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
