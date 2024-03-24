package com.milktea.main.user.service;

import com.milktea.main.user.dto.response.ProfileInfoResponse;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.FollowRepository;
import com.milktea.main.user.repository.UserRepository;
import com.milktea.main.util.exceptions.ValidationException;
import com.milktea.main.util.security.jwt.JwtTokenAdministrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final JwtTokenAdministrator jwtTokenAdministrator;

    public ProfileInfoResponse getProfileInfo(String username, String token) {
        if (Objects.isNull(token) || token.isBlank())
            return getProfileWithAnonymousUser(username);
        else return getProfileWithLoginUser(username, token);
    }

    private ProfileInfoResponse getProfileWithAnonymousUser(String username) {
        return new ProfileInfoResponse(
                userRepository.findByUsername(username).orElseThrow(() ->
                    new ValidationException(ValidationException.ErrorType.NOT_FOUND_USERNAME,
                        "username",
                        "해당하는 username을 가진 사용자를 찾을 수 없습니다.")
                )
        );
    }

    private ProfileInfoResponse getProfileWithLoginUser(String username, String token) {
        String email = String.valueOf(jwtTokenAdministrator
                .verifyToken(token)
                .get("email"));

        User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new ValidationException(ValidationException.ErrorType.INVALID_EMAIL,
                "email",
                "인증 정보의 email을 가진 사용자를 찾을 수 없습니다."));

        User getUser = userRepository.findByEmail(email).orElseThrow(() -> new ValidationException(ValidationException.ErrorType.NOT_FOUND_USERNAME,
                "username",
                "해당하는 username을 가진 사용자를 찾을 수 없습니다."));

        boolean hasFollow = false;
        if (followRepository.findByFollowerAndFollowing(getUser, currentUser).isPresent()) hasFollow = true;

        return new ProfileInfoResponse(getUser, hasFollow);
    }
}
