package com.project.danatix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import com.project.danatix.models.Role;
import com.project.danatix.models.User;
import com.project.danatix.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")

public class ProfileChangeBackendIntegrationTest {

    private ObjectMapper om;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        om = new ObjectMapper();
        User user = prepareUser();
        mockAuthenticationContext(user);
    }

    private User prepareUser() {

        Optional<User> user = userRepository.findUserByEmail("test@email.com");

        if (userRepository.findUserByEmail("test@email.com").isPresent()) {
            return userRepository.findUserByEmail("test@email.com").get();
        } else {
            User testUser = new User();
            testUser.setName("Johny Test");
            testUser.setEmail("test@email.com");
            testUser.setPassword(passwordEncoder.encode("password123"));
            testUser.setRole(Role.ROLE_USER);
            testUser.setEmailVerified(true);

            return userRepository.save(testUser);
        }
    }

    private void mockAuthenticationContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    @Test
    public void handlePatchRequestWithCorrectData() throws Exception {
        UserDataReceivedDTO newUserData = new UserDataReceivedDTO(
                "John Doe", "johndoe@test.com", "password123");

        mockMvc.perform(
                        patch("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(newUserData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("johndoe@test.com")));
    }
}
