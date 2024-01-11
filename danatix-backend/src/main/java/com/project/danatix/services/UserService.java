package com.project.danatix.services;

import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import com.project.danatix.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends UserDetailsService {

    void validateUserEmail(UserDataReceivedDTO userData);

    User getUserByEmail(String email);

    User createNewUser(UserDataReceivedDTO userData);

    List<String> profileChangeValidation(UserDataReceivedDTO userData);

    User modifyUser(String username, UserDataReceivedDTO userData);

    boolean doesEmailExist(String email);

    void generateEmailVerificationTokenForUser(User user);

    void verifyUserEmail(String token);

    List<String> verifyUserPassword(String name, String newPassword);
}
