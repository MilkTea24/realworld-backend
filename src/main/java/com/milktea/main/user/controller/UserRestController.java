package com.milktea.main.user.controller;

import com.milktea.main.user.dto.request.UserInfoDTO;
import com.milktea.main.user.dto.request.UserLoginRequest;
import com.milktea.main.user.dto.request.UserUpdateRequest;
import com.milktea.main.user.dto.response.UserInfoResponse;
import com.milktea.main.user.dto.response.UserLoginResponse;
import com.milktea.main.user.dto.request.UserRegisterRequest;
import com.milktea.main.user.dto.response.UserRegisterResponse;
import com.milktea.main.user.dto.response.UserUpdateResponse;
import com.milktea.main.user.service.UserService;
import com.milktea.main.util.security.EmailPasswordAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    //인증 관련 핵심 로직은 InitialAuthenticationFilter에 있음
    //컨트롤러 부분은 단순 User 정보 반환
    @PostMapping("/api/users/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginRequest.UserLoginDTO userDTO = userLoginRequest.userLoginDTO();
        UserLoginResponse response = userService.getLoginUser(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/users")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        UserRegisterRequest.UserRegisterDTO userDTO = userRegisterRequest.userRegisterDTO();
        UserRegisterResponse response = userService.registerUser(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //API 명세 상 users가 아님 user임 주의!
    @GetMapping("/api/user")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal String email, HttpServletRequest request) {
        UserInfoDTO userDTO = new UserInfoDTO(email);
        String token = request.getHeader("Authentication");
        UserInfoResponse response = userService.getCurrentUser(userDTO, token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/api/user")
    public ResponseEntity<?> updateUserInfo(@Valid @RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        UserUpdateRequest.UserUpdateDTO userDTO = userUpdateRequest.userUpdateDTO();
        String token = request.getHeader("Authentication");
        //토큰을 새로 발급하기 위해서는 email 뿐만 아니라 authority도 필요해서 @AuthenticationPrincipal 안쓰고 직접 가져왔다.
        EmailPasswordAuthentication auth = (EmailPasswordAuthentication) SecurityContextHolder.getContext().getAuthentication();
        UserUpdateResponse response = userService.updateUser(auth, userDTO, token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
