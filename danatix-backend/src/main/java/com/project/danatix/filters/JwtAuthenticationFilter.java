package com.project.danatix.filters;

import com.project.danatix.services.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/** Looks for authorization header and tries to validate it*/
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServiceImpl jwtService;
    private final UserDetailsService userService;

    public JwtAuthenticationFilter(UserDetailsService userService, JwtServiceImpl jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,@NonNull HttpServletResponse response, @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final int NUMBER_OF_CHARACTERS_IN_HEADER_BEFORE_TOKEN = 7;


        //If it doesn't find the Bearer token in authorization header it leaves request unauthorized and passes
        //it to next filter in filter chain
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Extracts the jwt token from header and user email from the token
        jwt = authHeader.substring(NUMBER_OF_CHARACTERS_IN_HEADER_BEFORE_TOKEN);
        userEmail = jwtService.extractEmail(jwt);

        //If email was successfully extracted and user has not been authorized in this request yet, proceeds to
        //authorize the token
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //If user with given email is not found, throws error
            UserDetails user = userService.loadUserByUsername(userEmail);

            //Token validation happens in jwtService
            if(jwtService.isTokenValid(jwt, user)) {

                //If jwt token is validated, proceeds to create authToken that contains info about the authenticated user
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //Stores the information in SecurityContextHolder and effectively marks user as authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
