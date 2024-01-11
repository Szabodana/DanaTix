package com.project.danatix;

import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import com.project.danatix.models.Role;
import com.project.danatix.models.User;
import com.project.danatix.repositories.UserRepository;
import com.project.danatix.services.UserServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserServiceUnitTests {

    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }


    @Test
    public void validateUserEmailThrowsExceptionForEmailAlreadyTaken() {
        User existingUser = new User("John Doe", "johndoe@test.com", "password123");
        UserDataReceivedDTO newUserData = new UserDataReceivedDTO("Jane Doe", "johndoe@test.com", "password");

        when(userRepository.findUserByEmail("johndoe@test.com")).thenReturn(Optional.of(existingUser));

        Exception e = assertThrows(IllegalArgumentException.class, () -> userService.validateUserEmail(newUserData));

        assertEquals("Email is already taken.", e.getMessage());

    }

    @Test
    public void validateUserEmailThrowsNoExceptionForUniqueEmail() {
        UserDataReceivedDTO newUserData = new UserDataReceivedDTO("Jane Doe", "johndoe@test.com", "password");

        when(userRepository.findUserByEmail("johndoe@test.com")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userService.validateUserEmail(newUserData));

    }

    @Test
    public void createNewUser() {

        UserDataReceivedDTO userData = new UserDataReceivedDTO("John Doe", "johndoe@test.com", "password123");
        User expected = new User(userData);
        expected.setPassword(passwordEncoder.encode(userData.getPassword()));
        expected.setRole(Role.ROLE_USER);

        when(userRepository.save(any(User.class))).thenReturn(expected);

        User newUser = userService.createNewUser(userData);

        assertEquals(expected, newUser);
    }

    @Test
    public void profileChangeValidationWithCorrectData() {
        UserDataReceivedDTO userData = new UserDataReceivedDTO("John Doe",
                "johndoe@test.com",
                "password123");

        List<String> result = userService.profileChangeValidation(userData);

        assertTrue(result.isEmpty());
    }

    @Test
    public void profileChangeValidationWithShortPassword() {
        UserDataReceivedDTO userData = new UserDataReceivedDTO("John Doe",
                "johndoe@test.com",
                "pwd");

        List<String> result = userService.profileChangeValidation(userData);

        assertEquals("Password must be at least 8 characters.", result.get(0));
    }

    @Test
    public void profileChangeValidationWithExistingMail() {
        UserDataReceivedDTO userData = new UserDataReceivedDTO("John Doe",
                "jeno.jeno@jenomail.com",
                "password");

        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);

        List<String> result = userService.profileChangeValidation(userData);

        assertEquals("Email is already taken.", result.get(0));
    }

    @Test
    public void modifyUserWithNonExistingMail() {
        UserDataReceivedDTO userData = new UserDataReceivedDTO("John Doe",
                "jeno.jeno@jenomail.com",
                "password");

        when(userRepository.findUserByEmail(any(String.class)))
                .thenThrow(new UsernameNotFoundException("There's no user with this email."));

        try {
            userService.modifyUser(userData.getEmail(), userData);
        } catch (UsernameNotFoundException e) {
            assertEquals("There's no user with this email.", e.getMessage());
        }
    }

    @Test
    public void modifyUserWithExistingMail() {
        UserDataReceivedDTO userNewData = new UserDataReceivedDTO("John Doe",
                "john.doe@test.com",
                "password");

        User userOldData = new User("jenoke",
                "jeno.jeno@jenomail.com",
                "password123");

        when(userRepository.findUserByEmail(any(String.class)))
                .thenReturn(Optional.of(userOldData));

        userService.modifyUser(userOldData.getEmail(), userNewData);

        assertEquals("john.doe@test.com", userOldData.getEmail());
        assertEquals("John Doe", userOldData.getName());
    }

    @Test
    public void generateEmailVerificationTokenForUser() {
        User user = new User();
        user.setName("Test user");
        user.setEmail("email@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);

        userService.generateEmailVerificationTokenForUser(user);

        assertNotNull(user.getEmailVerificationToken());
        assertNotNull(user.getEmailTokenExpiration());
    }

    @Test
    public void verifyUserEmailWithCorrectToken() {
        final int SEVENTY_TWO_HOURS_IN_MILLISECONDS = 1000 * 60 * 72;
        final String TOKEN = "this-is-token";

        User user = new User();
        user.setName("Test user");
        user.setEmail("email@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setEmailVerificationToken(TOKEN);
        user.setEmailTokenExpiration(new Date(System.currentTimeMillis() + SEVENTY_TWO_HOURS_IN_MILLISECONDS));

        when(userRepository.findUserByEmailVerificationToken(TOKEN))
                .thenReturn(Optional.of(user));

        userService.verifyUserEmail(TOKEN);

        assertTrue(user.isEmailVerified());
    }

    @Test
    public void verifyUserEmailWithIncorrectToken() {
        final int SEVENTY_TWO_HOURS_IN_MILLISECONDS = 1000 * 60 * 72;
        final String TOKEN = "this-is-token";

        User user = new User();
        user.setName("Test user");
        user.setEmail("email@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setEmailVerificationToken(TOKEN);
        user.setEmailTokenExpiration(new Date(System.currentTimeMillis() + SEVENTY_TWO_HOURS_IN_MILLISECONDS));

        when(userRepository.findUserByEmailVerificationToken(TOKEN))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.verifyUserEmail("this-is-NOT-token"));

        assertEquals("Token does not match any registered email.", exception.getMessage());
        assertFalse(user.isEmailVerified());
    }

    @Test
    public void verifyUserEmailWithExpiredToken() {
        final int SEVENTY_TWO_HOURS_IN_MILLISECONDS = 1000 * 60 * 72;
        final String TOKEN = "this-is-token";

        User user = new User();
        user.setName("Test user");
        user.setEmail("email@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setEmailVerificationToken(TOKEN);
        user.setEmailTokenExpiration(new Date(System.currentTimeMillis() - SEVENTY_TWO_HOURS_IN_MILLISECONDS));

        when(userRepository.findUserByEmailVerificationToken(TOKEN))
                .thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.verifyUserEmail(TOKEN));

        assertEquals("Token has expired.", exception.getMessage());
        assertFalse(user.isEmailVerified());
    }

}
