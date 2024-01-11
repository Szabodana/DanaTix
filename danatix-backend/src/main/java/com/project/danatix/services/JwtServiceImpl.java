package com.project.danatix.services;

import com.project.danatix.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/** Service for generating, reading and validating JWT tokens*/
@Service
public class JwtServiceImpl implements JwtService{

    final Environment environment;


    public JwtServiceImpl(Environment environment) {
        this.environment = environment;
    }


    @Override
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    /** Extracts any one given field from token's payload
     While extracting claims, it decodes the token under the hood and while doing that
     also verifies the signature and checks expiration*/
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(
            User user
    ) {
        final int TWENTY_FOUR_HOURS_IN_MILLISECONDS = 1000 * 60 * 24;

        return Jwts
                .builder()

                //Setting up custom claims
                .claim("userId", user.getId())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("isEmailVerified", user.isEmailVerified())

                //Sets expiration to 24 hours from now
                .setExpiration(new Date(System.currentTimeMillis() + TWENTY_FOUR_HOURS_IN_MILLISECONDS))

                //Generates signature, HS256 is standard algorithm
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Checks if email in token payload is same as email of provided user and if token has not expired*/
    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        try{
            final String username = extractEmail(token);

            //Since the user was retrieved from the database by the email extracted from token (see JwtAuthenticationFilter)
            //it seems redundant to check the email again, but supposedly it is for the purpose of re-usability of this
            //method and to make "double sure"
            return username.equals(user.getUsername());

            //If the token is expired, attempting to extract email from it will throw exception
        }catch (ExpiredJwtException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Provides Signing key for token signature using the Secret Key set up above*/
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(environment.getProperty("jwt.secret-key"));
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
