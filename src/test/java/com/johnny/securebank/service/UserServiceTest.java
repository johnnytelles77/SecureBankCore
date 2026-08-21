package com.johnny.securebank.service;

import com.johnny.securebank.dto.CreateUserRequestDTO;
import com.johnny.securebank.dto.UserResponseDTO;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_shouldCreateUserSuccessfully() {
        /// ARRANGE
        CreateUserRequestDTO request = new CreateUserRequestDTO(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );

        savedUser.setId(1L);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        /// ACT
        UserResponseDTO result = userService.registerUser(request);

        /// ASSERT
        assertEquals(1L, result.getId());
        assertEquals("Johnny", result.getFirstName());
        assertEquals("Telles", result.getLastName());
        assertEquals("johnny@test.com", result.getEmail());
        assertEquals(Role.CUSTOMER, result.getRole());

        verify(userRepository).save(any(User.class));
        verify(userRepository).existsByEmail(request.getEmail());

    }

    @Test
    void registerUser_shouldThrowExceptionWhenEmailAlreadyExists() {

    }
}