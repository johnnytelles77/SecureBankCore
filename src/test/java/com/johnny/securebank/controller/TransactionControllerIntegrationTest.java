package com.johnny.securebank.controller;

import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.AccountType;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.TransactionRepository;
import com.johnny.securebank.repository.UserRepository;
import com.johnny.securebank.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtService jwtService;

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
        String token = jwtService.generateToken(savedUser);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + token)
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
        String token = jwtService.generateToken(savedUser);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/withdraw")
                        .header("Authorization", "Bearer " + token)
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
        String token = jwtService.generateToken(savedUser);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        Account savedAccount2 = new Account(
                "ACC-1002",
                50.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.CHECKING,
                AccountStatus.ACTIVE
        );
        savedAccount2 = accountRepository.save(savedAccount2);

        mockMvc.perform(post("/transactions/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("""
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
        String token = jwtService.generateToken(savedUser);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {
                                "fromAccountId": %d,
                                "toAccountId": %d,
                                "amount": 50.0
                                }
                                """.formatted(savedAccount.getId(), 9999L)))
                .andExpect(status().isNotFound());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(100.0, updatedAccount.getBalance());
    }

    @Test
    void withdraw_shouldReturn403WhenCustomerDoesNotOwnAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);


        User savedUser2 = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser2 = userRepository.save(savedUser2);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/withdraw")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                                {
                                "accountId": %d,
                                "amount": 50.0
                                }
                                """.formatted(savedAccount.getId())))
                .andExpect(status().isForbidden());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(100.0, updatedAccount.getBalance());
    }

    @Test
    void withdraw_shouldAllowWhenCustomerOwnsAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/withdraw")
                .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                        "accountId": %d,
                                        "amount": 50.0
                                        }
                                        """.formatted(savedAccount.getId())))
                .andExpect(status().isCreated());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(50.0, updatedAccount.getBalance());
    }

    @Test
    void withdraw_shouldAllowWhenUserIsAdmin() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.ADMIN
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);


        User savedUser2 = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser2 = userRepository.save(savedUser2);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/withdraw")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(
                                """
                                        {
                                        "accountId": %d,
                                        "amount": 50.0
                                        }
                                        """.formatted(savedAccount.getId())))
                .andExpect(status().isCreated());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(50.0, updatedAccount.getBalance());
    }

    @Test
    void transfer_shouldReturn403WhenCustomerDoesNotOwnSourceAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);


        User savedUser2 = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser2 = userRepository.save(savedUser2);

        User savedUser3 = new User(
                "Leslie",
                "Paramo",
                "leslie@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser3 = userRepository.save(savedUser3);

        Account savedAccount = new Account(
                "ACC-1002",
                100.0,
                savedUser2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        Account savedAccount2 = new Account(
                "ACC-1001",
                20.0,
                savedUser3,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount2 = accountRepository.save(savedAccount2);

        mockMvc.perform(post("/transactions/transfer")
                .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                        "fromAccountId": %d,
                                        "toAccountId": %d,
                                        "amount": 50.0
                                        }
                                        """.formatted(savedAccount.getId(), savedAccount2.getId())))
                .andExpect(status().isForbidden());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();
        Account updatedAccount2 = accountRepository.findById(savedAccount2.getId()).orElseThrow();

        assertEquals(100.0, updatedAccount.getBalance());
        assertEquals(20.0, updatedAccount2.getBalance());
    }

    @Test
    void transfer_shouldAllowWhenCustomerOwnsSourceAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);


        User savedUser2 = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser2 = userRepository.save(savedUser2);

        Account savedAccount = new Account(
                "ACC-1002",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        Account savedAccount2 = new Account(
                "ACC-1001",
                20.0,
                savedUser2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount2 = accountRepository.save(savedAccount2);

        mockMvc.perform(post("/transactions/transfer")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
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
        assertEquals(70.0, updatedAccount2.getBalance());
    }

    @Test
    void transfer_shouldAllowWhenUserIsAdmin() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.ADMIN
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);


        User savedUser2 = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser2 = userRepository.save(savedUser2);

        User savedUser3 = new User(
                "Leslie",
                "Paramo",
                "leslie@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser3 = userRepository.save(savedUser3);

        Account savedAccount = new Account(
                "ACC-1002",
                100.0,
                savedUser2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        Account savedAccount2 = new Account(
                "ACC-1001",
                20.0,
                savedUser3,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount2 = accountRepository.save(savedAccount2);

        mockMvc.perform(post("/transactions/transfer")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
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
        assertEquals(70.0, updatedAccount2.getBalance());
    }

    @Test
    void deposit_shouldReturn403WhenCustomerDoesNotOwnAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);


        User savedUser2 = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser2 = userRepository.save(savedUser2);

        Account savedAccount = new Account(
                "ACC-1002",
                100.0,
                savedUser2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                        "accountId": %d,
                                        "amount": 50.0
                                        }
                                        """.formatted(savedAccount.getId())))
                .andExpect(status().isForbidden());

        Account updatedAccount = accountRepository.findById(savedAccount.getId()).orElseThrow();

        assertEquals(100.0, updatedAccount.getBalance());
    }

    @Test
    void deposit_shouldAllowWhenCustomerOwnsAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "accountId": %d,
                              "amount": 50.0
                            }
                            """.formatted(savedAccount.getId())))
                .andExpect(status().isCreated());

        Account updatedAccount = accountRepository
                .findById(savedAccount.getId())
                .orElseThrow();

        assertEquals(150.0, updatedAccount.getBalance());
    }

    @Test
    void deposit_shouldAllowWhenUserIsAdmin() throws Exception {
        User admin = new User(
                "Johnny",
                "Telles",
                "admin@test.com",
                "hashed-password",
                Role.ADMIN
        );
        admin = userRepository.save(admin);
        String token = jwtService.generateToken(admin);

        User customer = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        customer = userRepository.save(customer);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                customer,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(post("/transactions/deposit")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "accountId": %d,
                              "amount": 50.0
                            }
                            """.formatted(savedAccount.getId())))
                .andExpect(status().isCreated());

        Account updatedAccount = accountRepository
                .findById(savedAccount.getId())
                .orElseThrow();

        assertEquals(150.0, updatedAccount.getBalance());
    }

    @Test
    void getTransactions_shouldReturn403WhenCustomerDoesNotOwnAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);

        User customer = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        customer = userRepository.save(customer);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                customer,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(get("/transactions/account/{id}", savedAccount.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTransactions_shouldAllowWhenCustomerOwnsAccount() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(get("/transactions/account/{id}", savedAccount.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactions_shouldAllowWhenUserIsAdmin() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "hashed-password",
                Role.ADMIN
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);

        User customer = new User(
                "Juan",
                "Perez",
                "juan@test.com",
                "hashed-password",
                Role.CUSTOMER
        );
        customer = userRepository.save(customer);

        Account savedAccount = new Account(
                "ACC-1001",
                100.0,
                customer,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(get("/transactions/account/{id}", savedAccount.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
