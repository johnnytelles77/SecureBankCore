package com.johnny.securebank.controller;

import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.AccountType;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void findAllAccounts() throws Exception {
        mockMvc.perform(get("/accounts")).andExpect(status().isOk());
    }

    @Test
    public void findAccountById() throws Exception {
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
                0.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(get("/accounts/{id}", savedAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccount.getId()))
                .andExpect(jsonPath("$.accountNumber").value(savedAccount.getAccountNumber()));
    }

    @Test
    public void findAccountById_shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        mockMvc.perform(get("/accounts/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
