package com.johnny.securebank.controller;

import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUsers_shouldReturnOk() throws Exception {

        mockMvc.perform(get("/users"))
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

        mockMvc.perform(get("/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.firstName").value(savedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(savedUser.getLastName()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    void getUserById_shouldReturn404WhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/99999"))
                .andExpect(status().isNotFound());
    }
}
