package com.milktea.main.util.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {
    //두 개의 생성자를 가진 AuthenticationToken을 반환함
    private final BoardUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    //322 page
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //자격 증명으로 사용자가 전송한 아이디, 비밀번호
        String email = authentication.getName();
        String password = String.valueOf(authentication.getCredentials());

        //기존에 저장된 사용자 정보
        //저장된 사용자가 없는 경우는 userDetailsService에서 처리
        BoardUserDetails user = userDetailsService.loadUserByUsername(email);

        //비밀번호 맞는지 체크
        boolean checkPasswordResult = checkPassword(user, password);
        log.debug("사용자가 전달한 비밀번호와 저장된 비밀번호의 일치 여부 - {}", checkPasswordResult);
        log.debug("사용자가 전달한 비밀번호 - {}", password);
        log.debug("DB에 저장된 비밀번호 - {}", user.getPassword());

        //비밀번호 맞으면
        if (checkPasswordResult) {
            return new EmailPasswordAuthentication(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities());
        } else {
            log.debug("비밀번호가 일치하지 않음!");
            throw new BadCredentialsException("잘못된 이메일 또는 비밀번호입니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailPasswordAuthentication.class.isAssignableFrom(authentication);
    }


    private boolean checkPassword(BoardUserDetails user, String password) {
        return passwordEncoder.matches(password/*raw*/, user.getPassword()/*encoded*/);
    }
}
