package com.project.danatix;

import com.project.danatix.models.User;
import com.project.danatix.services.JwtService;
import com.project.danatix.services.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import io.jsonwebtoken.security.SignatureException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtServiceUnitTests {

    @Mock
    private Environment environment;
    private JwtService jwtService;
    private String validUnexpiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsIm5hbWUiOiJKb2huIiwiZW1haWwiOiJqb2huQHRlc3QuY29tIn0.rpIoyiS7VkLvLDORyGPCg1PPeAcVkSE8lU9k6lz6n7Q";
    private String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsIm5hbWUiOiJKb2huIiwiZW1haWwiOiJqb2hueUB0ZXN0LmNvbSJ9.pX23_nCwePfDIpKervoALxNqft0hCtUPPZqKVl0qp-k";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtServiceImpl(environment);
        when(environment.getProperty("jwt.secret-key")).thenReturn("Pdy1yzZW1lja+T+zI3IeHkiWaH0sqYoUlIl9VKy8sio=");
    }

    @Test
    public void extractEmailReturnsCorrectEmailForValidToken() {
        String expectedEmail = "john@test.com";
        String extractedEmail = jwtService.extractEmail(validUnexpiredToken);
        assertEquals(expectedEmail, extractedEmail);
    }

    @Test
    public void extractEmailThrowsExceptionForInvalidToken() {
        assertThrows(SignatureException.class, () -> jwtService.extractEmail(invalidToken));
    }

    @Test
    public void extractEmailReturnsNullForMissingClaim() {
        String tokenWithoutEmailClaim = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsIm5hbWUiOiJKb2huIn0.cDlpMscpO1OUvdDmp4SKZ3z0KxlCIu_XXXzLytBZUko";
        String extractedEmail = jwtService.extractEmail(tokenWithoutEmailClaim);
        assertNull(extractedEmail);
    }

    @Test
    public void extractClaimReturnsCorrectClaimForValidTokenAndValidClaimResolver() {
        String expectedClaim = "John";
        String extractedClaim = jwtService.extractClaim(validUnexpiredToken, claims -> claims.get("name", String.class));
        assertEquals(expectedClaim, extractedClaim);
    }

    @Test
    public void extractClaimThrowsExceptionForInvalidToken() {
        assertThrows(SignatureException.class, () -> jwtService.extractClaim(invalidToken, claims -> claims.get("name", String.class)));
    }

    @Test
    public void extractClaimReturnsNullForMissingClaim() {
        String tokenWithoutNameClaim = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoiam9obkB0ZXN0LmNvbSJ9.HHJE79DmC0jBJ_kRL031sCQq-1YMjFQH2twXxFNQODM";
        String extractedEmail = jwtService.extractClaim(tokenWithoutNameClaim, claims -> claims.get("name", String.class));
        assertNull(extractedEmail);
    }

    @Test
    public void generateTokenReturnsTokenWithCorrectClaims() {
        User user = new User();
        user.setId(123);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        String token = jwtService.generateToken(user);
        Long userId = jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));
        String name = jwtService.extractClaim(token, claims -> claims.get("name", String.class));
        String email = jwtService.extractEmail(token);

        assertNotNull(token);
        assertEquals(123, userId ); // Implement extractUserId method
        assertEquals("John Doe", name); // Implement extractName method
        assertEquals("john.doe@example.com", email); // Implement extractEmail method
    }

    @Test
    public void isTokenValidReturnsTrueForValidToken() {
        User user = new User();
        user.setId(1);
        user.setName("John");
        user.setEmail("john@test.com");
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    public void isTokenValidReturnsFalseForExpiredToken() {
        User user = new User();
        user.setId(1);
        user.setName("John");
        user.setEmail("john@test.com");

        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsIm5hbWUiOiJKb2huIiwiZW1haWwiOiJqb2huQHRlc3QuY29tIiwiZXhwIjoxNjk1NzY3MzcyfQ.ictYg8v0sBHICK-ys_9TGW3mGBs5iGTWiKrC_o5q67U";

        assertFalse(jwtService.isTokenValid(expiredToken, user));
    }

}
