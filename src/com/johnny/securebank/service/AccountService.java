package com.johnny.securebank.service;

import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account already exists!");
        }
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(0.0);
        account.setCreatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account updateAccount(Long id, Account updatedAccount) {

        Account existingAccount = getAccountById(id);

        existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        existingAccount.setType(updatedAccount.getType());
        existingAccount.setStatus(updatedAccount.getStatus());

        return accountRepository.save(existingAccount);
    }

    public void deleteAccount(Long id) {
        Account existingAccount = getAccountById(id);
        accountRepository.delete(existingAccount);
    }
}
