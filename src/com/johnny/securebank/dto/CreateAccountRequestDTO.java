package com.johnny.securebank.dto;

import com.johnny.securebank.model.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAccountRequestDTO {
    @NotBlank
    private String accountNumber;

    @NotNull
    private AccountType type;

    @NotNull
    private Long userId;

    public CreateAccountRequestDTO() {}

    public CreateAccountRequestDTO(String accountNumber, AccountType type, Long userId) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public AccountType getType() {
        return type;
    }
    public void setType(AccountType type) {
        this.type = type;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}