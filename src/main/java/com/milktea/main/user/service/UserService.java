package com.milktea.main.user.service;

import com.milktea.main.user.dto.request.UserInfoDTO;
import com.milktea.main.user.dto.request.UserLoginRequest;
import com.milktea.main.user.dto.request.UserUpdateRequest;
import com.milktea.main.user.dto.response.UserInfoResponse;
import com.milktea.main.user.dto.response.UserLoginResponse;
import com.milktea.main.user.dto.request.UserRegisterRequest;
import com.milktea.main.user.dto.response.UserRegisterResponse;
import com.milktea.main.user.dto.response.UserUpdateResponse;
import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.AuthorityRepository;
import com.milktea.main.user.repository.UserRepository;
import com.milktea.main.util.exceptions.ValidationException;
import com.milktea.main.util.security.jwt.JwtTokenAdministrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final JwtTokenAdministrator jwtTokenAdministrator;

    //Bean으로 등록된 passwordEncoder로 인코딩 과정을 거치고 userRepository에 생성한 User를 보냄
    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest.UserRegisterDTO userRequest) {
        //이미 가입된 Username이 있는지 확인
        checkDuplicateUsername(userRequest.username());

        //이미 가입된 이메일이 있는지 확인
        checkDuplicateEmail(userRequest.email());

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

        //인증을 한 회원이 데이터베이스에 유저가 없다면(이런 경우는 없을 것이라 예상되지만 혹시 몰라 예외 처리)
        if (findUser.isEmpty()) throwUserNotFoundException();

        return new UserLoginResponse(new UserLoginResponse.UserLoginDTO(findUser.get()));
    }

    public UserInfoResponse getCurrentUser(UserInfoDTO userRequest, String token) {
        String currentUserEmail = userRequest.email();

        Optional<User> findUser = userRepository.findByEmail(currentUserEmail);

        //인증을 한 회원이 데이터베이스에 유저가 없다면(이런 경우는 없을 것이라 예상되지만 혹시 몰라 예외 처리)
        if (findUser.isEmpty()) throwUserNotFoundException();

        return new UserInfoResponse(new UserInfoResponse.UserInfoDTO(findUser.get(), token));
    }

    //중요!!!!! 현재 방식은 기존의 정보를 null로 변경할 수 없다. null로 들어오면 변경하지 않는 필드로 간주하기 때문
    //만약에 email을 업데이트하게 되면 토큰을 다시 발급받아야 할 것 같은데..?
    //response로 새로운 token을 반환해 주어야 할 것 같다.
    @Transactional
    public UserUpdateResponse updateUser(Authentication auth, UserUpdateRequest.UserUpdateDTO userRequest, String token) {
        //수정하려는 email이 이미 가입된 email인지 확인
        if (Objects.nonNull(userRequest.email())) checkDuplicateEmail(userRequest.email());

        //수정하려는 username이 이미 가입된 username인지 확인
        if (Objects.nonNull(userRequest.username())) checkDuplicateUsername(userRequest.username());

        Optional<User> findUserOp = userRepository.findByEmail(auth.getName());
        if (findUserOp.isEmpty()) throwUserNotFoundException();
        User findUser = findUserOp.get();

        findUser.updateUser(userRequest);
        
        String newToken = jwtTokenAdministrator.updateToken(auth, userRequest.email(), token);

        return new UserUpdateResponse(new UserUpdateResponse.UserUpdateDTO(findUser, newToken));
    }

    private void throwUserNotFoundException() {
        log.error("인증을 통과한 유저의 정보를 데이터베이스에서 찾을 수 없습니다. 즉시 원인을 파악해야 합니다.");
        throw new ValidationException(ValidationException.ErrorType.NOT_FOUND_USERNAME, "username", "해당하는 username을 찾을 수 없습니다.");
    }

    private void checkDuplicateUsername(String registerUsername) {
        if (userRepository.findByUsername(registerUsername).isPresent())
            throw new ValidationException(ValidationException.ErrorType.DUPLICATE_USERNAME, "username", "이미 존재하는 username입니다.");
    }

    private void checkDuplicateEmail(String registerEmail) {
        if (userRepository.findByEmail(registerEmail).isPresent())
            throw new ValidationException(ValidationException.ErrorType.DUPLICATE_EMAIL, "email", "이미 존재하는 email입니다.");
    }


}
