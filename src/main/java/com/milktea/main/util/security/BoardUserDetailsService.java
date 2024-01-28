package com.milktea.main.util.security;

import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public BoardUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> supplier =
                () -> new UsernameNotFoundException(
                        "잘못된 이메일 또는 비밀번호입니다.");

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(supplier);

        return new BoardUserDetails(user);
    }
}
