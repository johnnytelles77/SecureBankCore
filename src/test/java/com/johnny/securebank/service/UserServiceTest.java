package com.johnny.securebank.service;

import com.johnny.securebank.dto.CreateUserRequestDTO;
import com.johnny.securebank.dto.UpdateUserRequestDTO;
import com.johnny.securebank.dto.UserResponseDTO;
import com.johnny.securebank.exception.DuplicateEmailException;
import com.johnny.securebank.exception.UserNotFoundException;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        CreateUserRequestDTO request = new CreateUserRequestDTO(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_shouldReturnUserWhenExists() {
        User user = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("johnny@test.com", result.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(1L)
        );

        verify(userRepository).findById(1L);
    }

    @Test
    void getUsers_shouldReturnUsers() {
        User user = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        user.setId(1L);

        User user2 = new User(
                "Daniel",
                "Paramo",
                "daniel@test.com",
                "12345678",
                Role.CUSTOMER
        );
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        List<UserResponseDTO> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("johnny@test.com", result.get(0).getEmail());

        verify(userRepository).findAll();
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {
        User user = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        user.setId(1L);

        UpdateUserRequestDTO updateUser = new UpdateUserRequestDTO(
                "Daniel",
                "Telles",
                "johnny@test.com",
                Role.CUSTOMER
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.updateUser(1L, updateUser);

        assertEquals("Daniel", result.getFirstName());
        assertEquals("Telles", result.getLastName());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(1L, new UpdateUserRequestDTO()));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {
        User user = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L));

        verify(userRepository, never()).delete(any(User.class));
    }
}