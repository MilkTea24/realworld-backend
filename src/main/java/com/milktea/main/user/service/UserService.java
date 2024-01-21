package com.milktea.main.user.service;

import com.milktea.main.user.dto.UserLoginRequest;
import com.milktea.main.user.dto.UserRegisterRequest;
import com.milktea.main.user.dto.UserRegisterResponse;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import com.milktea.main.util.exceptions.ValidationException;
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
    public UserRegisterResponse registerUser(UserRegisterRequest userRequest) {
        //이미 가입된 Username이 있는지 확인
        checkDuplicateUsername(userRequest);

        User saveUser = User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .bio(userRequest.bio())
                .image(userRequest.image())
                .build();

        saveUser.setPassword(userRequest.password(), passwordEncoder);

        User dbUser = userRepository.save(saveUser);

        return new UserRegisterResponse(dbUser);
    }

    private void checkDuplicateUsername(UserRegisterRequest userRequest) {
        String registerUsername = userRequest.username();
        if (userRepository.findByUsername(registerUsername).isPresent())
            throw new ValidationException(ValidationException.ErrorType.DUPLICATE_USERNAME, "username", "이미 존재하는 username입니다.");
    }

    public void login(UserLoginRequest userLoginRequest) {

    }
}
