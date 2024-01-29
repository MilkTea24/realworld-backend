package com.milktea.main.user.service;

import com.milktea.main.user.dto.request.UserInfoDTO;
import com.milktea.main.user.dto.request.UserLoginRequest;
import com.milktea.main.user.dto.response.UserInfoResponse;
import com.milktea.main.user.dto.response.UserLoginResponse;
import com.milktea.main.user.dto.request.UserRegisterRequest;
import com.milktea.main.user.dto.response.UserRegisterResponse;
import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.AuthorityRepository;
import com.milktea.main.user.repository.UserRepository;
import com.milktea.main.util.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    //Bean으로 등록된 passwordEncoder로 인코딩 과정을 거치고 userRepository에 생성한 User를 보냄
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest.UserRegisterDTO userRequest) {
        //이미 가입된 Username이 있는지 확인
        checkDuplicateUsername(userRequest);

        //이미 가입된 이메일이 있는지 확인
        checkDuplicateEmail(userRequest);

        User saveUser = User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .bio(userRequest.bio())
                .image(userRequest.image())
                .build();

        //지금 서비스는 USER 권한 밖에 없고 클라이언트에서 원하는 권한을 전달하지 않으므로 당장은 USER 권한만 있다고 가정
        Authority userAuthority = new Authority("USER");

        //비밀번호 추가
        saveUser.setPassword(userRequest.password(), passwordEncoder);

        //권한 추가
        saveUser.addAuthority(userAuthority);

        authorityRepository.save(userAuthority);
        User dbUser = userRepository.save(saveUser);



        return new UserRegisterResponse(new UserRegisterResponse.UserRegisterDTO(dbUser));
    }

    public UserLoginResponse getLoginUser(UserLoginRequest.UserLoginDTO userRequest) {
        String loginEmail = userRequest.email();

        Optional<User> findUser = userRepository.findByEmail(loginEmail);

        if (findUser.isEmpty()) {
            log.error("인증을 통과한 유저의 정보를 데이터베이스에서 찾을 수 없습니다. 즉시 원인을 파악해야 합니다.");
            throw new RuntimeException("사용자 정보를 찾는 중 문제가 발생하였습니다.");
        }

        return new UserLoginResponse(new UserLoginResponse.UserLoginDTO(findUser.get()));
    }

    private void checkDuplicateUsername(UserRegisterRequest.UserRegisterDTO userRequest) {
        String registerUsername = userRequest.username();
        if (userRepository.findByUsername(registerUsername).isPresent())
            throw new ValidationException(ValidationException.ErrorType.DUPLICATE_USERNAME, "username", "이미 존재하는 username입니다.");
    }

    private void checkDuplicateEmail(UserRegisterRequest.UserRegisterDTO userRequest) {
        String registerEmail = userRequest.email();
        if (userRepository.findByEmail(registerEmail).isPresent())
            throw new ValidationException(ValidationException.ErrorType.DUPLICATE_EMAIL, "email", "이미 존재하는 email입니다.");
    }
}
