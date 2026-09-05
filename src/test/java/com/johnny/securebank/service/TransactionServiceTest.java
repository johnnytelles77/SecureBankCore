package com.johnny.securebank.service;

import com.johnny.securebank.dto.TransactionResponseDTO;
import com.johnny.securebank.exception.AccountNotFoundException;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.Transaction;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.AccountType;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.model.enums.TransactionType;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void deposit_shouldDepositSuccessfully() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        Transaction savedTransaction = new Transaction(
                1L,
                50.00,
                "Deposit to account",
                TransactionType.DEPOSIT,
                null,
                savedAccount
        );
        savedTransaction.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDTO result = transactionService.deposit(
                1L,
                50.0
        );

        assertEquals(1L, result.getId());
        assertEquals(50.0, result.getAmount());
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(150.0, savedAccount.getBalance());

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void deposit_shouldThrowExceptionWhenAccountDoesNotExist() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.deposit(1L, 50.0)
        );

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withdraw_shouldWithdrawSuccessfully() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                200.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount.setId(1L);

        Transaction savedTransaction = new Transaction(
                1L,
                50.0,
                "Withdraw from account",
                TransactionType.WITHDRAW,
                savedAccount,
                null
        );

        savedTransaction.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDTO result = transactionService.withdraw(
                1L,
                50.0
        );

        assertEquals(1L, result.getId());
        assertEquals(50.0, result.getAmount());
        assertEquals(TransactionType.WITHDRAW, result.getType());
        assertEquals(150.0, savedAccount.getBalance());

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_shouldThrowExceptionWhenAccountDoesNotExist() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.withdraw(1L, 50.0));
        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void withdraw_shouldThrowExceptionWhenInsufficientBalance() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                30.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );


        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(1L, 50.0)
        );

        assertEquals(30.0, savedAccount.getBalance());

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_shouldTransferSuccessfully() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                200.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        Account savedAccount2 = new Account(
                2L,
                "ACC-1002",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.CHECKING,
                AccountStatus.ACTIVE
        );

        Transaction savedTransaction = new Transaction(
                1L,
                50.0,
                "Transfer from account 1 to account 2",
                TransactionType.TRANSFER,
                savedAccount,
                savedAccount2
        );
        savedTransaction.setCreatedAt(LocalDateTime.now());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(savedAccount2));
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDTO result = transactionService.transfer(
                1L,
                2L,
                50.0
        );

        assertEquals(1L, result.getId());
        assertEquals(50.0, result.getAmount());
        assertEquals(TransactionType.TRANSFER, result.getType());
        assertEquals(150.0, savedAccount.getBalance());
        assertEquals(150.0, savedAccount.getBalance());
        assertEquals(150.0, savedAccount2.getBalance());
        assertEquals(1L, result.getFromAccountId());
        assertEquals(2L, result.getToAccountId());

        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenFromAccountDoesNotExist() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.transfer(1L, 2L, 50.0)
        );
        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).findById(2L);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenToAccountDoesNotExist() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                200.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

       assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.transfer(1L, 2L, 50.0));

       verify(accountRepository).findById(1L);
       verify(accountRepository).findById(2L);
       verify(accountRepository, never()).save(any(Account.class));
       verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transfer_shouldThrowExceptionWhenInsufficientBalance() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                30.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        Account savedAccount2 = new Account(
                2L,
                "ACC-1002",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.CHECKING,
                AccountStatus.ACTIVE
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(savedAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(savedAccount2));

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.transfer(1L, 2L, 50.0)
        );

        assertEquals(30.0, savedAccount.getBalance());
        assertEquals(100.0, savedAccount2.getBalance());

        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getTransactionsByAccountId_shouldReturnTransactionsWhenAccountExists() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                150.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        Transaction savedTransaction = new Transaction(
                1L,
                50.0,
                "Deposit to account",
                TransactionType.DEPOSIT,
                null,
                savedAccount
        );

        savedTransaction.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(savedAccount));

        when(transactionRepository.findByFromAccountIdOrToAccountId(1L, 1L))
                .thenReturn(List.of(savedTransaction));

        List<TransactionResponseDTO> result =
                transactionService.getTransactionsByAccountId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(50.0, result.get(0).getAmount());
        assertEquals(TransactionType.DEPOSIT, result.get(0).getType());
        assertNull(result.get(0).getFromAccountId());
        assertEquals(1L, result.get(0).getToAccountId());

        verify(accountRepository).findById(1L);
        verify(transactionRepository)
                .findByFromAccountIdOrToAccountId(1L, 1L);
    }

    @Test
    void getTransactionsByAccountId_shouldThrowExceptionWhenAccountDoesNotExist() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.getTransactionsByAccountId(1L)
        );

        verify(accountRepository).findById(1L);

        verify(transactionRepository, never())
                .findByFromAccountIdOrToAccountId(1L, 1L);
    }
}
