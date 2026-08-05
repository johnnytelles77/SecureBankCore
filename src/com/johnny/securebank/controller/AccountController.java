package com.johnny.securebank.controller;

import com.johnny.securebank.dto.AccountResponseDTO;
import com.johnny.securebank.dto.CreateAccountRequestDTO;
import com.johnny.securebank.dto.UpdateAccountStatusRequestDTO;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponseDTO createAccount(@Valid @RequestBody CreateAccountRequestDTO request) {
        return accountService.createAccount(request);
    }

    @GetMapping
    public List<AccountResponseDTO> getAccounts() {
        return  accountService.getAccounts();
    }

    @GetMapping("/{id}")
    public AccountResponseDTO getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PatchMapping("/{id}/status")
    public AccountResponseDTO updateAccountStatus(@PathVariable Long id, @Valid @RequestBody UpdateAccountStatusRequestDTO request) {
        return accountService.updateAccountStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public AccountResponseDTO closeAccount(@PathVariable Long id) {
        return accountService.closeAccount(id);
    }
}
