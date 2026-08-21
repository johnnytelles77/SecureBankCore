package com.johnny.securebank.exception;

public class InsufficientFundsException extends SecureBankException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
