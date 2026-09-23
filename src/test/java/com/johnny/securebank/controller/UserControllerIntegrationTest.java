package com.johnny.securebank.controller;

import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.UserRepository;
import com.johnny.securebank.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Test
    void getUsers_shouldReturnOk() throws Exception {
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
                .andExpect(status().isOk());
    }

    @Test
    void getUsers_shouldReturnUserWhenExists() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);

        mockMvc.perform(get("/users/" + savedUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.firstName").value(savedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(savedUser.getLastName()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    void getUserById_shouldReturn404WhenUserDoesNotExist() throws Exception {
        User savedUser = new User(
                "Johnny",
                "Telles",
                "johnny@test.com",
                "12345678",
                Role.CUSTOMER
        );
        savedUser = userRepository.save(savedUser);
        String token = jwtService.generateToken(savedUser);

        mockMvc.perform(get("/users/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerUser_shouldStoreHashedPassword() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                "firstName": "Johnny",
                                "lastName": "Telles",
                                "email": "johnnyhash@test.com",
                                "password": "12345678",
                                "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isCreated());

        User savedUser = userRepository.findByEmail("johnnyhash@test.com")
                .orElseThrow();
        assertNotEquals("12345678", savedUser.getPassword());

        assertTrue(
                passwordEncoder.matches("12345678",
                        savedUser.getPassword())
        );
    }
}
