package com.johnny.securebank.controller;

import com.johnny.securebank.dto.AccountResponseDTO;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.service.AccountService;
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
    public AccountResponseDTO createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping
    public List<AccountResponseDTO> getAccounts() {
        return  accountService.getAccounts();
    }

    @GetMapping("/{id}")
    public AccountResponseDTO getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @PutMapping("/{id}")
    public AccountResponseDTO updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }

    @DeleteMapping("/{id}")
    public void deleteAccountById(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}
