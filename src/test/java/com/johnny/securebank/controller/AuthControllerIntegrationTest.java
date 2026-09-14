package com.johnny.securebank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johnny.securebank.dto.LoginRequestDTO;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        String encodedPassword = passwordEncoder.encode("password");
        User savedUser = new User(
                "Johnny",
                "Telles",
                "auth-test@test.com",
                encodedPassword,
                Role.CUSTOMER
        );
        userRepository.save(savedUser);
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO(
                "auth-test@test.com",
                "password");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_shouldReturnUnauthorizedWhenPasswordIsInvalid() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO(
                "auth-test@test.com",
                "wrong-password"
        );
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnUnauthorizedWhenEmailDoesNotExist() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO(
                "johnnyt77@test.com",
                "password"
        );
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isUnauthorized());
    }

}
