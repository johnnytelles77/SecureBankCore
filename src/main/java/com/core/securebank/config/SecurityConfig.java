package com.core.securebank.config;

import com.core.securebank.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(csrf -> csrf.disable());
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
        );
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()

                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/*").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users/*").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/users/*").hasAuthority("ADMIN")

                .requestMatchers(HttpMethod.POST, "/accounts").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/accounts/*/status").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/accounts/*").hasAuthority("ADMIN")

                .anyRequest().authenticated()
        );
        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }
}
