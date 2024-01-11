package com.project.danatix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.danatix.models.DTOs.NewUserDTO;
import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import com.project.danatix.repositories.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
class RegisterBackendIntegrationTests {

    private ObjectMapper om;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        om = new ObjectMapper();
    }

    @AfterAll
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void POSTregisterWithValidUserData() throws Exception {
        UserDataReceivedDTO newUserData = new UserDataReceivedDTO(
                "John Doe", "johnunique@test.com", "password123");
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(newUserData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("johnunique@test.com")));

        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    public void POSTregisterWithMissingName() throws Exception {
        UserDataReceivedDTO newUserData = new UserDataReceivedDTO();
        newUserData.setEmail("johndoe@test.com");
        newUserData.setPassword("password123");

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(newUserData)))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Name is required.")));
    }

    @Test
    public void POSTregisterWithNoData() throws Exception {

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Name, email and password are required.")));
    }

    @Test
    public void POSTregisterWithNoRequestBody() throws Exception {

        mockMvc.perform(
                        post("/api/register"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Name, email and password are required.")));
    }

    @Test
    public void POSTregisterWithPasswordUnder8Characters() throws Exception {
        UserDataReceivedDTO newUserData = new UserDataReceivedDTO(
                "John Doe", "short@test.com", "pass123");


        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(newUserData)))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Password must be at least 8 characters.")));
    }

    @Test
    public void POSTregisterWithEmailAlreadyTaken() throws Exception {
        UserDataReceivedDTO newUserData = new UserDataReceivedDTO(
                "John Doe", "same@test.com", "password123");

        UserDataReceivedDTO anotherNewUserData = new UserDataReceivedDTO(
                "Jane Doe", "same@test.com", "password123");


        mockMvc.perform(
                post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newUserData)));

        mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(newUserData)))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Email is already taken.")));
    }

}
