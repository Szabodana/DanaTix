package com.project.danatix.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class PatchRequestMatcher implements RequestMatcher {

    private final String endpoint;

    public PatchRequestMatcher(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return "PATCH".equals(request.getMethod()) && endpoint.equals(request.getRequestURI());
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        return RequestMatcher.super.matcher(request);
    }
}
