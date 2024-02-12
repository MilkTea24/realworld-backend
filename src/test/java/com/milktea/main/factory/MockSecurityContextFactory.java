package com.milktea.main.factory;

import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import com.milktea.main.util.exceptions.ExceptionUtils;
import com.milktea.main.util.security.BoardUserDetails;
import com.milktea.main.util.security.EmailPasswordAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public class MockSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SecurityContext createSecurityContext(WithCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.getContext();

        UserMother.UserBuilder userBuilder = UserMother.user();

        //하드코딩 해버림
        //현재 API는 고정되어 있기도 하고 리플렉션으로 구현하기에는 비밀번호 인코딩과 배열(authority)과 같은 특수한 상황도 고려해야함
        if (!annotation.email().isBlank()) userBuilder.withEmail(annotation.email());
        if (!annotation.password().isBlank()) userBuilder.withPassword(annotation.password(), passwordEncoder);
        if (!annotation.bio().isBlank()) userBuilder.withBio(annotation.bio());
        if (!annotation.image().isBlank()) userBuilder.withBio(annotation.image());
        if (annotation.authority().length != 0) userBuilder.withAuthorities(Arrays.stream(annotation.authority())
                .map(Authority::new)
                .toList());


        BoardUserDetails boardUserDetails = new BoardUserDetails(userBuilder.build());
        Authentication auth = new EmailPasswordAuthentication(boardUserDetails.getUsername(),
                boardUserDetails.getPassword(),
                boardUserDetails.getAuthorities());

        context.setAuthentication(auth);

        return context;
    }
}
