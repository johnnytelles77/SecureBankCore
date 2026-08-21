package com.johnny.securebank.exception;

public class DuplicateEmailException extends SecureBankException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
