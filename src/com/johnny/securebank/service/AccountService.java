package com.johnny.securebank.service;

import com.johnny.securebank.dto.AccountResponseDTO;
import com.johnny.securebank.exception.AccountNotFoundException;
import com.johnny.securebank.exception.DuplicateAccountException;
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

    private AccountResponseDTO convertToResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getType(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));
    }

    public AccountResponseDTO createAccount(Account account) {
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new DuplicateAccountException("Account already exists!");
        }
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(0.0);
        account.setCreatedAt(LocalDateTime.now());

        Account savedAccount = accountRepository.save(account);
        return convertToResponseDTO(savedAccount);
    }

    public AccountResponseDTO getAccountById(Long id) {
        Account account = findAccountById(id);
        return convertToResponseDTO(account);
    }

    public List<AccountResponseDTO> getAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public AccountResponseDTO updateAccount(Long id, Account updatedAccount) {

        Account existingAccount = findAccountById(id);

        existingAccount.setAccountNumber(updatedAccount.getAccountNumber());
        existingAccount.setType(updatedAccount.getType());
        existingAccount.setStatus(updatedAccount.getStatus());

        Account savedAccount = accountRepository.save(existingAccount);
        return convertToResponseDTO(savedAccount);
    }

    public void deleteAccount(Long id) {
        Account existingAccount = findAccountById(id);
        accountRepository.delete(existingAccount);
    }
}
