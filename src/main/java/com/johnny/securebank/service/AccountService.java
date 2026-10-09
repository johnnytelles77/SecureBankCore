package com.johnny.securebank.service;

import com.johnny.securebank.dto.AccountResponseDTO;
import com.johnny.securebank.dto.CreateAccountRequestDTO;
import com.johnny.securebank.dto.UpdateAccountStatusRequestDTO;
import com.johnny.securebank.exception.AccountNotFoundException;
import com.johnny.securebank.exception.DuplicateAccountException;
import com.johnny.securebank.exception.ForbiddenOperationException;
import com.johnny.securebank.exception.UserNotFoundException;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private static final Logger log =
            LoggerFactory.getLogger(AccountService.class);

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
        log.info(
                "Account created: accountId={}, userId={}",
                savedAccount.getId(),
                user.getId());
        return convertToResponseDTO(savedAccount);
    }

    public AccountResponseDTO getAccountById(Long id) {
        Account account = findAccountById(id);
        validateAccountOwnership(account);

        return convertToResponseDTO(account);
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private void validateAccountOwnership(Account account) {
        User authenticatedUser = getAuthenticatedUser();
        if (authenticatedUser.getRole() != Role.ADMIN &&
                !authenticatedUser.getId().equals(account.getUser().getId())) {
            throw new ForbiddenOperationException("You do not have permission to access this account");
        }
    }

    public List<AccountResponseDTO> getAccounts() {
        User authenticatedUser = getAuthenticatedUser();

        List<Account> accounts;

        if (authenticatedUser.getRole() == Role.ADMIN) {
            accounts = accountRepository.findAll();
        } else {
            accounts = accountRepository.findByUserId(authenticatedUser.getId());
        }
        return accounts.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public AccountResponseDTO updateAccountStatus(Long id, UpdateAccountStatusRequestDTO request) {
        Account existingAccount = findAccountById(id);

        existingAccount.setStatus(request.getStatus());
        Account savedAccount = accountRepository.save(existingAccount);
        log.info(
                "Account status updated: accountId={}, status={}",
                savedAccount.getId(),
                savedAccount.getStatus());
        return convertToResponseDTO(savedAccount);
    }

    public AccountResponseDTO closeAccount(Long id) {
        Account existingAccount = findAccountById(id);

        existingAccount.setStatus(AccountStatus.CLOSED);

        Account savedAccount = accountRepository.save(existingAccount);
        log.info(
                "Account closed: accountId={}",
                savedAccount.getId());
        return convertToResponseDTO(savedAccount);
    }
}
