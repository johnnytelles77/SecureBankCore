package com.johnny.securebank.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;
import com.johnny.securebank.model.User;
import io.jsonwebtoken.Jwts;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[]  KeyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(KeyBytes);
    }

    public String generateToken(User user) {
        Date issuedAt = new Date();
        Date expirationDate = new Date(issuedAt.getTime() + expiration);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }
}
