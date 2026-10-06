package com.johnny.securebank.service;

import com.johnny.securebank.dto.LoginRequestDTO;
import com.johnny.securebank.dto.LoginResponseDTO;
import com.johnny.securebank.exception.InvalidCredentialsException;
import com.johnny.securebank.model.User;
import com.johnny.securebank.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Failed authentication attempt for email: {}", request.getEmail());

                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed authentication attempt for email: {}", request.getEmail());

            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        log.info("User authenticated successfully: {}", user.getEmail());

        return new LoginResponseDTO(token);
    }
}