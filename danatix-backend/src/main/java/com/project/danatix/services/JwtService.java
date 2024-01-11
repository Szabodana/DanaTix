package com.project.danatix.services;

import com.project.danatix.models.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface JwtService {
    String extractEmail(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(
            User user
    );

    boolean isTokenValid(String token, UserDetails user);
}
