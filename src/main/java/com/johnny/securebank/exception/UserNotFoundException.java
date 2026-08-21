package com.johnny.securebank.exception;

public class UserNotFoundException extends SecureBankException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
