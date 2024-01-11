package com.project.danatix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    private final Environment environment;
    public CorsConfig(Environment environment) {
        this.environment = environment;
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        String allowedOrigin = environment.getProperty("allowed.origin");

        if (allowedOrigin != null && !allowedOrigin.isEmpty()) {
            config.addAllowedOrigin(allowedOrigin);
        }

        // Allows requests from frontend
        config.addAllowedOrigin("");

        // Allows common HTTP methods GET, POST, PUT, DELETE...
        config.addAllowedMethod("*");

        config.addAllowedHeader("*");

        // Allow credentials
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}


