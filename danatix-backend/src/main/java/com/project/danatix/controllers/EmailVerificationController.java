package com.project.danatix.controllers;

import com.project.danatix.models.User;
import com.project.danatix.services.EmailService;
import com.project.danatix.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    private final UserService userService;
    private final EmailService emailService;

    public EmailVerificationController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map> errors(Exception e) {
        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());

        return ResponseEntity.status(401).body(result);
    }

    @PatchMapping
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {

        userService.verifyUserEmail(token);

        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> sendVerificationEmail(@AuthenticationPrincipal User user) {
        userService.generateEmailVerificationTokenForUser(user);
        emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());

        return ResponseEntity.ok().build();
    }
}
