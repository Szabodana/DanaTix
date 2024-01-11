package com.project.danatix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.danatix.models.DTOs.LoginRequestDTO;
import com.project.danatix.models.Role;
import com.project.danatix.models.User;
import com.project.danatix.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
public class LoginBackendIntegrationTests {

    private User user = new User();
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
        setUpUser();
    }

    @AfterAll
    public void tearDown() {
        userRepository.deleteAll();
    }

    private void setUpUser() {
        if(userRepository.findUserByEmail("johndoe@test.com").isEmpty()) {
            user.setName("John");
            user.setEmail("johndoe@test.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(Role.ROLE_USER);
            user.setEmailVerified(true);
            userRepository.save(user);
        }
    }

    @Test
    void contextLoads() {
    }

    @Test
    void POSTloginWithValidCredentials() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("johndoe@test.com");
        request.setPassword("password");

        mockMvc.perform(
                post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void POSTloginWithWrongCredentials() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("johndoe@test.com");
        request.setPassword("passwordNot");

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(request)))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Email or password is incorrect.")));
    }

    @Test
    void POSTloginWithMissingPassword() throws Exception {

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \n" +
                                        "    \"email\":\"john@test.com\"\n" +
                                        "}"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Password is required.")));
    }

    @Test
    void POSTloginWithMissingEmail() throws Exception {

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \n" +
                                        "    \"password\":\"password\"\n" +
                                        "}"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Email is required.")));
    }

    @Test
    void POSTloginWithEmptyRequestBody() throws Exception {

        mockMvc.perform(
                        post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("All fields are required.")));
    }

    @Test
    void POSTloginWithNoRequestBody() throws Exception {

        mockMvc.perform(
                        post("/api/login"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("All fields are required.")));
    }
}
