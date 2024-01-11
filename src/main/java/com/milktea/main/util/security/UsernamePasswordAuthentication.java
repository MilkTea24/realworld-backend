package com.milktea.main.util.security;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UsernamePasswordAuthentication extends UsernamePasswordAuthenticationToken {
    //이 생성자를 호출하면 Authentication 객체가 인증됨
    public UsernamePasswordAuthentication(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    //이 생성자를 호출하면 인증이 완료되지 않음
    public UsernamePasswordAuthentication(
            Object principal,
            Object credentials) {
        super(principal, credentials);
    }
}
