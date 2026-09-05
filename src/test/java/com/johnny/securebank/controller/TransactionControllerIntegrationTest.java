package com.johnny.securebank.controller;

import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.AccountType;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.TransactionRepository;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void deposit_shouldIncreaseAccountBalance() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "accountId": %d,
                                "amount": 50.0
                                }
                                """.formatted(savedAccount.getId())))
                .andExpect(status().isCreated());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(150.0, updatedAccount.getBalance());
    }

    @Test
    void withdraw_shouldDecreaseAccountBalance() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                "accountId": %d,
                                "amount": 40.0
                                }
                                """.formatted(savedAccount.getId())))
                .andExpect(status().isCreated());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();
        assertEquals(60.0, updatedAccount.getBalance());
    }

    @Test
    void transfer_shouldMoveMoneyBetweenAccounts() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        Account savedAccount2 = new Account(
                2L,
                "ACC-1002",
                50.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.CHECKING,
                AccountStatus.ACTIVE
        );
        savedAccount2 = accountRepository.save(savedAccount2);

        mockMvc.perform(post("/transactions/transfer").contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                "fromAccountId": %d,
                                "toAccountId": %d,
                                "amount": 50.0
                                }
                                """.formatted(savedAccount.getId(), savedAccount2.getId())))
                .andExpect(status().isCreated());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();
        Account updatedAccount2 = accountRepository.findById(savedAccount2.getId()).orElseThrow();

        assertEquals(50.0, updatedAccount.getBalance());
        assertEquals(100.0, updatedAccount2.getBalance());
    }

    @Test
    void transfer_shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);

        Account savedAccount = new Account(
                1L,
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/transfer").contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                "fromAccountId": %d,
                                "toAccountId": %d,
                                "amount": 50.0
                                }
                                """.formatted(savedAccount.getId(), 9999L)))
                .andExpect(status().isNotFound());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(100.0, savedAccount.getBalance());
    }
}
