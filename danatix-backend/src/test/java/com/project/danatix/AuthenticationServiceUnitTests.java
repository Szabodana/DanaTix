package com.project.danatix;

import com.project.danatix.models.DTOs.AuthenticationResponseDTO;
import com.project.danatix.models.DTOs.LoginRequestDTO;
import com.project.danatix.models.User;
import com.project.danatix.repositories.UserRepository;
import com.project.danatix.services.AuthenticationService;
import com.project.danatix.services.AuthenticationServiceImpl;
import com.project.danatix.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthenticationServiceUnitTests {

    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationServiceImpl(userRepository, jwtService, authManager);
    }

    @Test
    public void loginReturnsAuthenticationResponseDTOForValidCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("johndoe@test.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail("johndoe@test.com");
        user.setPassword(passwordEncoder.encode("password"));

        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsIm5hbWUiOiJKb2huIiwiZW1haWwiOiJqb2huQHRlc3QuY29tIiwiZXhwIjoxNjk1NzY3MzcyfQ.ictYg8v0sBHICK-ys_9TGW3mGBs5iGTWiKrC_o5q67U";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                "johndoe@test.com", "password");

        when(userRepository.findUserByEmail("johndoe@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn(jwt);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);

        AuthenticationResponseDTO response = authenticationService.login(request);

        assertNotNull(response.getToken());
        assertEquals(jwt, response.getToken());

    }

    @Test
    public void loginThrowsExceptionForWrongCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("johndoe@test.com");
        request.setPassword("passwordNot");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException(""));

        assertThrows(BadCredentialsException.class, () -> authenticationService.login(request));
    }
}
