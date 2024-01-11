package com.project.danatix.controllers;

import com.project.danatix.models.DTOs.AuthenticationResponseDTO;
import com.project.danatix.models.DTOs.LoginRequestDTO;
import com.project.danatix.services.AuthenticationServiceImpl;
import com.project.danatix.utils.FieldErrorsExtractor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final AuthenticationServiceImpl authService;
    public LoginController(AuthenticationServiceImpl authService) {
        this.authService = authService;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map> noRequestBodyError() {

        Map<String, String> result = new HashMap<>();
        result.put("message", "All fields are required.");

        return ResponseEntity.status(401).body(result);
    }

    /** Handles exception coming from validating Request body and holding info of all failed fields*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map> requestBodyValidationError(MethodArgumentNotValidException e) {

        Map<String, String> result = new HashMap<>();
        List<String> failedFields = new FieldErrorsExtractor(e).getFailedFields();
        String firstField = StringUtils.capitalize(failedFields.get(0));

        //If more than one field fails, the message pattern changes
        String message = (failedFields.size() == 1)? firstField + " is required."
                : "All fields are required.";

        result.put("message", message);

        return ResponseEntity.status(401).body(result);
    }

    /** Handles exception coming from UsernamePasswordAuthenticationFilter*/
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map> badCredentialsError(Exception e) {

        Map<String, String> result = new HashMap<>();

        result.put("message", "Email or password is incorrect.");

        return ResponseEntity.status(401).body(result);
    }

    @PostMapping
    public ResponseEntity<AuthenticationResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
