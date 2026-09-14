package com.johnny.securebank.service;

import com.johnny.securebank.dto.LoginRequestDTO;
import com.johnny.securebank.dto.LoginResponseDTO;
import com.johnny.securebank.exception.InvalidCredentialsException;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    public void login_shouldReturnTokenWhenCredentialsAreValid(){

        LoginRequestDTO request = new LoginRequestDTO
                ("johnny@test.com",
                        "12345678");

        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );

        when(userRepository.findByEmail("johnny@test.com"))
                .thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("12345678", "hashed-password"))
                .thenReturn(true);
        when(jwtService.generateToken(savedUser)).thenReturn("token");

        LoginResponseDTO result = authService.login(request);

        assertEquals("token", result.getToken());

        verify(userRepository).findByEmail("johnny@test.com");
        verify(passwordEncoder).matches("12345678", "hashed-password");
        verify(jwtService).generateToken(savedUser);
    }

    @Test
    public void login_shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginRequestDTO request =
                new LoginRequestDTO("johnny@test.com", "wrong-password");

        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "hashed-password",
                Role.CUSTOMER
        );

        when(userRepository.findByEmail("johnny@test.com"))
                .thenReturn(Optional.of(savedUser));

        when(passwordEncoder.matches("wrong-password", "hashed-password"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );
        verify(jwtService, never()).generateToken(savedUser);
    }

    @Test
    public void login_shouldThrowExceptionWhenEmailDoesNotExist() {
        LoginRequestDTO request =
                new LoginRequestDTO("missing@test.com", "12345678");

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail("missing@test.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }
}
