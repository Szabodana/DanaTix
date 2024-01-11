package com.project.danatix;


import com.project.danatix.models.Role;
import com.project.danatix.models.User;
import com.project.danatix.repositories.UserRepository;
import com.project.danatix.services.EmailService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmailVerificationIntegrationTests {

    private final Date EXPIRATION_AFTER_TWENTY_FOUR_HOURS = new Date(System.currentTimeMillis() + 1000 * 60 * 24);
    private final Date EXPIRATION_TWENTY_FOUR_HOURS_AGO = new Date(System.currentTimeMillis() - 1000 * 60 * 24);
    private final String TOKEN = "this-is-token";

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userRepository.deleteAll();
    }

    @AfterAll
    public void tearDown() {
        userRepository.deleteAll();
    }

    private User setUpUser(String token, Date expiration) {
        User user = new User();
        user.setName("John");
        user.setEmail("johndoe@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
        user.setEmailVerificationToken(token);
        user.setEmailTokenExpiration(expiration);

        return userRepository.save(user);
    }

    private void mockAuthenticationContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );
    }

    @Test
    public void POSTemailVerificationWithAuthorizedRequestWithUnverifiedEmail() throws Exception {
        User user = setUpUser(TOKEN, EXPIRATION_AFTER_TWENTY_FOUR_HOURS);
        mockAuthenticationContext(user);
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        mockMvc.perform(
                post("/api/email-verification"))
                .andExpect(status().isOk());

        verify(javaMailSender).send(any(MimeMessage.class));

        assertNotNull(user.getEmailVerificationToken());
        assertNotEquals("this-is-token", user.getEmailVerificationToken());
        assertNotNull(user.getEmailTokenExpiration());
        assertNotEquals(EXPIRATION_AFTER_TWENTY_FOUR_HOURS, user.getEmailTokenExpiration());
    }

    @Test
    public void POSTemailVerificationWithUnauthorizedRequest() throws Exception {

        mockMvc.perform(
                        post("/api/email-verification"))
                .andExpect(status().is(403));
    }

    @Test
    public void PATCHemailVerificationWithUnauthorizedRequestAndUnverifiedEmailAndCorrectToken() throws Exception {
        setUpUser(TOKEN, EXPIRATION_AFTER_TWENTY_FOUR_HOURS);

        mockMvc.perform(
                        patch("/api/email-verification")
                                .param("token", TOKEN))
                .andExpect(status().isOk());

        assertTrue(userRepository.findUserByEmailVerificationToken(TOKEN).get().isEmailVerified());
    }

    @Test
    public void PATCHemailVerificationWithUnauthorizedRequestAndUnverifiedEmailAndWrongToken() throws Exception {
        setUpUser(TOKEN, EXPIRATION_AFTER_TWENTY_FOUR_HOURS);

        mockMvc.perform(
                        patch("/api/email-verification")
                                .param("token", "not-a-token"))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Token does not match any registered email.")));
    }

    @Test
    public void PATCHemailVerificationWithUnauthorizedRequestAndUnverifiedEmailAndExpiredToken() throws Exception {
        setUpUser(TOKEN, EXPIRATION_TWENTY_FOUR_HOURS_AGO);

        mockMvc.perform(
                        patch("/api/email-verification")
                                .param("token", TOKEN))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Token has expired.")));
    }

    @Test
    public void PATCHemailVerificationWithUnauthorizedRequestAndVerifiedEmail() throws Exception {
        User user = setUpUser(TOKEN, EXPIRATION_AFTER_TWENTY_FOUR_HOURS);
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(
                        patch("/api/email-verification")
                                .param("token", TOKEN))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message", is("Token has already been used, your email is already verified.")));
    }
}
