package com.johnny.securebank.service;

import com.johnny.securebank.dto.AccountResponseDTO;
import com.johnny.securebank.dto.CreateAccountRequestDTO;
import com.johnny.securebank.exception.AccountNotFoundException;
import com.johnny.securebank.exception.DuplicateAccountException;
import com.johnny.securebank.exception.UserNotFoundException;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,  UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
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

    public AccountResponseDTO createAccount(CreateAccountRequestDTO request) {
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new DuplicateAccountException("Account already exists!");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        Account account = new Account();

        account.setAccountNumber(request.getAccountNumber());
        account.setType(request.getType());
        account.setUser(user);

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
