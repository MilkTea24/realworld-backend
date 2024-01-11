package com.milktea.main.user.service;

import com.milktea.main.user.dto.UserLoginRequest;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    //Bean으로 등록된 passwordEncoder로 인코딩 과정을 거치고 userRepository에 생성한 User를 보냄
    @Transactional
    public void addUser(UserLoginRequest userRequest) {
        String encodedPassword = passwordEncoder.encode(userRequest.password());

        User saveUser = User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .bio(userRequest.bio())
                .image(userRequest.image())
                .password(encodedPassword)
                .build();


        userRepository.save(saveUser);
    }

    public void login(UserLoginRequest userLoginRequest) {

    }
}
