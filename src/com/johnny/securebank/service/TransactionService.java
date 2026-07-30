package com.johnny.securebank.service;

import com.johnny.securebank.dto.TransactionResponseDTO;
import com.johnny.securebank.exception.AccountNotFoundException;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.Transaction;
import com.johnny.securebank.model.enums.TransactionType;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

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

    public TransactionResponseDTO deposit(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException("Account not found"));
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
        return convertToResponseDTO(savedTransaction);
    }

    public TransactionResponseDTO withdraw(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException("Account not found"));
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
        return convertToResponseDTO(savedTransaction);
    }

    public TransactionResponseDTO transfer(Long fromAccountId, Long toAccountId, Double amount) {

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(()-> new AccountNotFoundException("From account not found"));
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
        return convertToResponseDTO(savedTransaction);
    }

    public List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }
}