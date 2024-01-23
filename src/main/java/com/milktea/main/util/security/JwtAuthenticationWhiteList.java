package com.milktea.main.util.security;

import org.springframework.security.web.util.matcher.RegexRequestMatcher;

public class JwtAuthenticationWhiteList {
    public static final String[] ALL_METHOD_WHITELIST = {
            "/api/users/login",
            "api/tags",
            "api/profiles/*"
    };

    public static final RegexRequestMatcher[] SPECIFIC_METHOD_WHITELIST = {
            new RegexRequestMatcher("/api/users", "POST"),
            new RegexRequestMatcher("/api/articles", "GET"),
            new RegexRequestMatcher("/api/articles/*", "GET"),
            new RegexRequestMatcher("/api/articles/*/comments", "GET")
    };
}
