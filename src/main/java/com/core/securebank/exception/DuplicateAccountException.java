package com.core.securebank.exception;

public class DuplicateAccountException extends SecureBankException {
    public DuplicateAccountException(String message) {
        super(message);
    }
}
