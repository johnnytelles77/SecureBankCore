package com.johnny.securebank.service;

import com.johnny.securebank.dto.TransactionResponseDTO;
import com.johnny.securebank.exception.AccountNotFoundException;
import com.johnny.securebank.exception.ForbiddenOperationException;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.Transaction;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.model.enums.TransactionType;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.TransactionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.util.List;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private static final Logger log =
            LoggerFactory.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    private TransactionResponseDTO convertToResponseDTO(Transaction transaction) {

        Long fromAccountId = transaction.getFromAccount() != null
                ? transaction.getFromAccount().getId()
                : null;

        Long toAccountId = transaction.getToAccount() != null
                ? transaction.getToAccount().getId()
                : null;

        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getType(),
                fromAccountId,
                toAccountId,
                transaction.getCreatedAt()
        );
    }

    @Transactional
    public TransactionResponseDTO deposit(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException("Account not found"));

        validateAccountOwnership(account);

        account.deposit(amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                null,
                amount,
                "Deposit to account",
                TransactionType.DEPOSIT,
                null,
                account
        );
        transaction.setCreatedAt(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info(
                "Deposit completed: accountId={}, amount={}",
                accountId,
                amount);
        return convertToResponseDTO(savedTransaction);
    }

    @Transactional
    public TransactionResponseDTO withdraw(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException("Account not found"));

        validateAccountOwnership(account);

        account.withdraw(amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                null,
                amount,
                "Withdraw from account",
                TransactionType.WITHDRAW,
                account,
                null
        );
        transaction.setCreatedAt(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info(
                "Withdraw completed: accountId={}, amount={}",
                accountId,
                amount);
        return convertToResponseDTO(savedTransaction);
    }

    @Transactional
    public TransactionResponseDTO transfer(Long fromAccountId, Long toAccountId, Double amount) {

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(()-> new AccountNotFoundException("From account not found"));

        validateAccountOwnership(fromAccount);

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(()-> new AccountNotFoundException("To account not found"));

        fromAccount.transferTo(toAccount, amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction(
                null,
                amount,
                "Transfer between accounts",
                TransactionType.TRANSFER,
                fromAccount,
                toAccount
        );
        transaction.setCreatedAt(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info(
                "Transfer completed: fromAccountId={}, toAccountId={}, amount={}",
                fromAccountId,
                toAccountId,
                amount
        );

        return convertToResponseDTO(savedTransaction);
    }

    public List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        validateAccountOwnership(account);

        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private User getAuthenticatedUser() {
        return  (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    private void validateAccountOwnership(Account account) {
        User authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() != Role.ADMIN
                && !authenticatedUser.getId().equals(account.getUser().getId())) {
            log.warn(
                    "Unauthorized account access attempt: userId={}, accountId={}",
                    authenticatedUser.getId(),
                    account.getId()
            );

            throw new ForbiddenOperationException("You do not have permission to access this account");
        }
    }
}