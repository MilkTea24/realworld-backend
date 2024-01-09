package com.milktea.main.util.security;

import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BoardUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public BoardUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> supplier =
                () -> new UsernameNotFoundException(
                        "인증 과정에서 문제가 발생하였습니다.");

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(supplier);

        return new BoardUserDetails(user);
    }
}
