package com.johnny.securebank.service;

import com.johnny.securebank.dto.AccountResponseDTO;
import com.johnny.securebank.dto.CreateAccountRequestDTO;
import com.johnny.securebank.dto.UpdateAccountStatusRequestDTO;
import com.johnny.securebank.exception.AccountNotFoundException;
import com.johnny.securebank.exception.DuplicateAccountException;
import com.johnny.securebank.exception.UserNotFoundException;
import com.johnny.securebank.model.Account;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.AccountStatus;
import com.johnny.securebank.model.enums.AccountType;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.AccountRepository;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;


    @Test
    void createAccount_shouldCreateAccountSuccessfully() {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
                "ACC-1001",
                AccountType.SAVINGS,
                1L
        );
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
                0.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        when(accountRepository.existsByAccountNumber(request.getAccountNumber())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountResponseDTO result = accountService.createAccount(request);

       assertEquals(1L, result.getId());
       assertEquals("ACC-1001", result.getAccountNumber());
       assertEquals(0.0,result.getBalance());
       assertEquals(AccountType.SAVINGS, result.getType());

       verify(accountRepository).existsByAccountNumber(request.getAccountNumber());
       verify(userRepository).findById(1L);
       verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrowExceptionWhenAccountNumberAlreadyExists() {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
                "ACC-1001",
                AccountType.SAVINGS,
                1L
        );
        when(accountRepository.existsByAccountNumber(request.getAccountNumber())).thenReturn(true);

        assertThrows(
                DuplicateAccountException.class,
                () -> accountService.createAccount(request)
        );

        verify(accountRepository).existsByAccountNumber(request.getAccountNumber());
        verify(userRepository, never()).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrowExceptionWhenUserDoesNotExist() {
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
                "ACC-1001",
                AccountType.SAVINGS,
                1L
        );

        when(accountRepository.existsByAccountNumber("ACC-1001")).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> accountService.createAccount(request)
        );

        verify(accountRepository).existsByAccountNumber("ACC-1001");
        verify(userRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountById_shouldReturnAccountWhenExists() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser.setId(1L);

        Account account = new Account(
                1L,
                "ACC-1001",
                0.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponseDTO result = accountService.getAccountById(1L);

        assertEquals(1L, result.getId());
        assertEquals("ACC-1001", result.getAccountNumber());

        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccountById_shouldThrowExceptionWhenAccountDoesNotExist() {

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountById(1L)
        );

        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccounts_shouldReturnAccountsWhenExists() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser.setId(1L);

        Account account = new Account(
                1L,
                "ACC-1002",
                0.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.CHECKING,
                AccountStatus.ACTIVE
        );

        Account account2 = new Account(
                2L,
                "ACC-1001",
                0.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        when(accountRepository.findAll()).thenReturn(Arrays.asList(account, account2));
        List<AccountResponseDTO> result = accountService.getAccounts();

        assertEquals(2, result.size());
        assertEquals("ACC-1002",  result.get(0).getAccountNumber());
        assertEquals("ACC-1001",  result.get(1).getAccountNumber());

        verify(accountRepository).findAll();
    }

    @Test
    void updateAccountStatus_shouldUpdateAccountStatusSuccessfully() {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser.setId(1L);

        Account account = new Account(
                1L,
                "ACC-1001",
                0.0,
                savedUser,
                LocalDateTime.now(),
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        UpdateAccountStatusRequestDTO request = new UpdateAccountStatusRequestDTO(
                AccountStatus.FROZEN
        );
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountResponseDTO result = accountService.updateAccountStatus(1L, request);

        assertEquals(AccountStatus.FROZEN, request.getStatus());

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }
}
