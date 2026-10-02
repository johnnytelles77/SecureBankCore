package com.johnny.securebank.security;

import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.AccountType;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.UserRepository;
import com.johnny.securebank.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void protectedEndpoint_shouldReturn401WhenTokenIsMissing() throws Exception {
        this.mockMvc.perform(get("/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldAllowAccessWithValidToken() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        this.mockMvc.perform(get("/accounts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_shouldReturn401WhenTokenIsInvalid() throws Exception {
        this.mockMvc.perform(get("/accounts")
                        .header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldReturn401WhenTokenIsExpired() throws Exception {

        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);

        User user = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);

        mockMvc.perform(get("/accounts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_shouldReturn403WhenUserIsCustomer() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_shouldAllowAccessWhenUserIsAdmin() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "admin@test.com",
                "12345678",
                Role.ADMIN
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateAccountStatus_shouldReturn403WhenUserIsCustomer() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        mockMvc.perform(patch("/accounts/999/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "status": "CLOSED"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateAccountStatus_shouldAllowAccessWhenUserIsAdmin() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "admin@test.com",
                "12345678",
                Role.ADMIN
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        Account savedAccount = new Account(
                "ACC-1001",
                0.0,
                user,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(patch("/accounts/{id}/status", savedAccount.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "status": "CLOSED"
                                }
                                """))
                .andExpect(status().isOk());

        Account updatedAccount = accountRepository.findById(savedAccount.getId())
                .orElseThrow();

        assertEquals(AccountStatus.CLOSED, updatedAccount.getStatus());
    }

    @Test
    void closeAccount_shouldReturn403WhenUserIsCustomer() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        mockMvc.perform(delete("/accounts/{id}", 999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void closeAccount_shouldAllowAccessWhenUserIsAdmin() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "admin@test.com",
                "12345678",
                Role.ADMIN
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        Account savedAccount = new Account(
                "ACC-1001",
                0.0,
                user,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(delete("/accounts/{id}", savedAccount.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Account updatedAccount = accountRepository.findById(savedAccount.getId())
                .orElseThrow();

        assertEquals(AccountStatus.CLOSED, updatedAccount.getStatus());
    }

    @Test
    void getAccount_shouldReturn403WhenCustomerDoesNotOwnAccount() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        User user2 = new User(
                "Juan",
                "perez",
                "customer2@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user2 = userRepository.save(user2);

        Account savedAccount = new Account(
                "ACC-1001",
                0.0,
                user2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(get("/accounts/{id}", savedAccount.getId())
                        .header("Authorization", "Bearer " + token)).
                andExpect(status().isForbidden());
    }

    @Test
    void getAccount_shouldAllowAccessWhenCustomerOwnsAccount() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        Account savedAccount = new Account(
                "ACC-1001",
                0.0,
                user,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(get("/accounts/{id}", savedAccount.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getAccount_shouldAllowAccessWhenUserIsAdmin() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "12345678",
                Role.ADMIN
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        User user2 = new User(
                "Juan",
                "perez",
                "customer2@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user2 = userRepository.save(user2);


        Account savedAccount = new Account(
                "ACC-1001",
                0.0,
                user2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        mockMvc.perform(get("/accounts/{id}", savedAccount.getId())
                .header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }

    @Test
    void getAccounts_shouldReturnOnlyAccountsOwnedByCustomer() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "customer1@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        User user2 = new User(
                "Juan",
                "perez",
                "customer2@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user2 = userRepository.save(user2);

        Account savedAccount = new Account(
                "ACC-1001",
                0.0,
                user,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        Account savedAccount2 = new Account(
                "ACC-1002",
                0.0,
                user,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount2 = accountRepository.save(savedAccount2);

        Account savedAccount3 = new Account(
                "ACC-1003",
                0.0,
                user2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount3 = accountRepository.save(savedAccount3);

        mockMvc.perform(get("/accounts")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-1001"))
                .andExpect(jsonPath("$[1].accountNumber").value("ACC-1002"));
    }

    @Test
    void getAccounts_shouldReturnAllAccountsWhenUserIsAdmin() throws Exception {
        User user = new User(
                "Johnny",
                "Telles",
                "admin@test.com",
                "12345678",
                Role.ADMIN
        );

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);

        User user2 = new User(
                "Juan",
                "perez",
                "customer2@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user2 = userRepository.save(user2);

        User user3 = new User(
                "Leslie",
                "Paramo",
                "customer3@test.com",
                "12345678",
                Role.CUSTOMER
        );

        user3 = userRepository.save(user3);

        Account savedAccount = new Account(
                "ACC-1001",
                0.0,
                user2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount = accountRepository.save(savedAccount);

        Account savedAccount2 = new Account(
                "ACC-1002",
                0.0,
                user2,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount2 = accountRepository.save(savedAccount2);

        Account savedAccount3 = new Account(
                "ACC-1003",
                0.0,
                user3,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        savedAccount3 = accountRepository.save(savedAccount3);

        mockMvc.perform(get("/accounts")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-1001"))
                .andExpect(jsonPath("$[1].accountNumber").value("ACC-1002"))
                .andExpect(jsonPath("$[2].accountNumber").value("ACC-1003"));
    }
}