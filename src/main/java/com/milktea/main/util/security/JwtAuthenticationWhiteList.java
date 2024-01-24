package com.milktea.main.util.security;

import org.springframework.security.web.util.matcher.RegexRequestMatcher;

public class JwtAuthenticationWhiteList {
    //모든 HTTP 메서드의 요청을 허용함
    public static final String[] ALL_METHOD_WHITELIST = {
            "/api/users/login",
            "/api/tags",
            "/api/profiles/*",
            "/h2-console/**" //테스트 용!!!!!!!!!!!!!!
    };

    //특정 HTTP 메서드의 요청만 허용함
    public static final RegexRequestMatcher[] SPECIFIC_METHOD_WHITELIST = {
            new RegexRequestMatcher("/api/users", "POST"),
            new RegexRequestMatcher("/api/articles", "GET"),
            new RegexRequestMatcher("/api/articles/*", "GET"),
            new RegexRequestMatcher("/api/articles/*/comments", "GET")
    };
}
