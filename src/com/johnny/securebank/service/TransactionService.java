package com.johnny.securebank.service;

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

    public Transaction deposit(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new IllegalArgumentException("Account not found"));
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
        return transactionRepository.save(transaction);
    }

    public Transaction withdraw(Long accountId, Double amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new IllegalArgumentException("Account not found"));
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
        return transactionRepository.save(transaction);
    }

    public Transaction transfer(Long fromAccountId, Long toAccountId, Double amount) {

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(()-> new IllegalArgumentException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(()-> new IllegalArgumentException("To account not found"));

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
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
    }
}