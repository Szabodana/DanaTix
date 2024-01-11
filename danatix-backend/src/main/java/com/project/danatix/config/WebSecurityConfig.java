package com.project.danatix.config;

import com.project.danatix.filters.EmailVerificationFilter;
import com.project.danatix.filters.JwtAuthenticationFilter;
import com.project.danatix.services.UserService;
import com.project.danatix.utils.PatchRequestMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationFilter emailVerificationFilter;
    private final CorsFilter corsFilter;

    public WebSecurityConfig(UserService userService, JwtAuthenticationFilter jwtAuthenticationFilter, PasswordEncoder passwordEncoder, EmailVerificationFilter emailVerificationFilter, CorsFilter corsFilter) {
        this.userService = userService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationFilter = emailVerificationFilter;
        this.corsFilter = corsFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(new AntPathRequestMatcher("/api/articles")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/register")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/login")).permitAll()
                        .requestMatchers(new PatchRequestMatcher("/api/email-verification")).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider())

                //Adds the two filters at the beginning of filter chain:

                //jwtAuthenticationFilter - looks for authorization header and validates it

                //UsernamePasswordAuthenticationFilter - intercepts request that include user credentials and passes them
                //to Authentication Manager that manages all set up Authentication Providers, using them
                //to authenticate the user credentials
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(emailVerificationFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(corsFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userService);
        daoProvider.setPasswordEncoder(passwordEncoder);
        return daoProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}