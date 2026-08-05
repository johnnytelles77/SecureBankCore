package com.johnny.securebank.dto;

import com.johnny.securebank.model.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateAccountStatusRequestDTO {

    @NotNull
    private AccountStatus status;

    public UpdateAccountStatusRequestDTO() {}
    public UpdateAccountStatusRequestDTO(AccountStatus status) {
        this.status = status;
    }

    public AccountStatus getStatus() {
        return status;
    }
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
