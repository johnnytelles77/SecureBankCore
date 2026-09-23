package com.johnny.securebank.security;

import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import com.johnny.securebank.repository.UserRepository;
import com.johnny.securebank.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.util.ReflectionTestUtils;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void protectedEndpoint_shouldReturn401WhenTokenIsMissing() throws Exception {
        this.mockMvc.perform(get("/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_shouldAllowAccessWithValidToken()  throws Exception {
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
}
