package com.johnny.securebank.service;
import com.johnny.securebank.model.User;
import com.johnny.securebank.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;


import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private static final String TEST_SECRET =
            "dGVzdC10ZXN0LXRlc3QtdGVzdC10ZXN0LXRlc3QtdGVzdC0xMjM0NTY3ODkwMTIzNA==";

   @BeforeEach
    void setUp() {
       jwtService = new JwtService();
               ReflectionTestUtils.setField(
                       jwtService,
                       "secret",
                       TEST_SECRET
               );

               ReflectionTestUtils.setField(
                       jwtService,
                       "expiration",
                       3600000L
               );
   }

   @Test
   void generateToken_shouldCreateTokenForUser() {
       User savedUser = new User(
               "Johnny",
               "Telles",
               "johnny@test.com",
               "hashed-password",
               Role.CUSTOMER
       );
       String token = jwtService.generateToken(savedUser);

       byte[] keyBytes = Decoders.BASE64.decode(TEST_SECRET);
       SecretKey key = Keys.hmacShaKeyFor(keyBytes);

       Claims claims = Jwts.parser()
               .verifyWith(key)
               .build()
               .parseSignedClaims(token)
               .getPayload();

       Date expiration = claims.getExpiration();
       Date issuedAt = claims.getIssuedAt();

       long tokenDuration = expiration.getTime() - issuedAt.getTime();

       assertNotNull(token);
       assertFalse(token.isBlank());
       assertEquals("johnny@test.com", claims.getSubject());
       assertEquals(
               "CUSTOMER",
               claims.get("role", String.class)
       );
       assertEquals(3600000L, tokenDuration);
   }

}
