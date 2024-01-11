package com.project.danatix.filters;

import com.project.danatix.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class EmailVerificationFilter extends OncePerRequestFilter {

    Map<String, String> exceptedEndpoints = new HashMap<>();

    public EmailVerificationFilter() {

        //To add more exceptions, simply put them in the map here, following the pattern bellow
        exceptedEndpoints.put("/api/login", "POST");
        exceptedEndpoints.put("/api/email-verification", "POST");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if(exceptedEndpoints.containsKey(uri) && exceptedEndpoints.get(uri).equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();

            if(!user.isEmailVerified()) {
                respondWithErrorMessage(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void respondWithErrorMessage(HttpServletResponse response) throws IOException {
        String jsonResponse = "{\n" +
                "    \"message\": \"Please verify your email.\",\n" +
                "}";

        // Sets response status and content type
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Writes the JSON response to the output stream
        try (PrintWriter writer = response.getWriter()) {
            writer.write(jsonResponse);
            writer.flush();
        }
    }
}
