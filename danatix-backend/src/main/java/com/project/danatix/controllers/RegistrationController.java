package com.project.danatix.controllers;

import com.project.danatix.models.DTOs.NewUserDTO;
import com.project.danatix.models.DTOs.UserDataReceivedDTO;
import com.project.danatix.models.User;
import com.project.danatix.services.EmailService;
import com.project.danatix.services.UserService;
import com.project.danatix.utils.FieldErrorsExtractor;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/register")
public class RegistrationController {

    private final UserService userService;
    private final EmailService emailService;

    public RegistrationController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    /** Handles exception coming from validating Request body and holding info of all failed fields*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map> requestBodyValidationError(MethodArgumentNotValidException e) {

        Map<String, String> result = new HashMap<>();
        FieldErrorsExtractor extractor = new FieldErrorsExtractor(e);
        List<String> failedFields = extractor.getFailedFields();

        //If more than one field failed, message pattern changes
        String message = (failedFields.size() == 1)? extractor.getFirstError().getDefaultMessage()
                : "Name, email and password are required.";

        result.put("message", message);

        return ResponseEntity.status(401).body(result);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<Map> noRequestBodyError(Exception e) {

        Map<String, String> result = new HashMap<>();
        result.put("message", "Name, email and password are required.");

        return ResponseEntity.status(401).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map> otherErrors(Exception e) {

        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());

        return ResponseEntity.status(401).body(result);
    }

    @PostMapping
    public ResponseEntity<NewUserDTO> createUser(@Valid @RequestBody UserDataReceivedDTO userData) {

        userService.validateUserEmail(userData);
        User user = userService.createNewUser(userData);
        NewUserDTO userDTO = new NewUserDTO(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getEmailVerificationToken());

        return ResponseEntity.ok(userDTO);
    }
}
