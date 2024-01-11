package com.project.danatix.services;

import com.project.danatix.models.DTOs.AuthenticationResponseDTO;
import com.project.danatix.models.DTOs.LoginRequestDTO;
import com.project.danatix.models.User;
import com.project.danatix.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    private final UserRepository userRepository;
    private final JwtService jwtService;

    //Manages the email and password authentication (see WebSecurityConfig)
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(UserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /** Verifies given user credentials and either throws exception or returns JWT token*/
    @Override
    public AuthenticationResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(

                //Uses this token to pass email and password for authentication
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponseDTO(jwtToken);
    }
}