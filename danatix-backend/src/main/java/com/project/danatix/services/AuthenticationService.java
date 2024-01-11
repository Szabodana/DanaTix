package com.project.danatix.services;

import com.project.danatix.models.DTOs.AuthenticationResponseDTO;
import com.project.danatix.models.DTOs.LoginRequestDTO;

public interface AuthenticationService {
    AuthenticationResponseDTO login(LoginRequestDTO request);
}
