package com.milktea.main.user.controller;

import com.milktea.main.user.dto.request.UserInfoDTO;
import com.milktea.main.user.dto.request.UserLoginRequest;
import com.milktea.main.user.dto.response.UserInfoResponse;
import com.milktea.main.user.dto.response.UserLoginResponse;
import com.milktea.main.user.dto.request.UserRegisterRequest;
import com.milktea.main.user.dto.response.UserRegisterResponse;
import com.milktea.main.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;

    //인증 관련 핵심 로직은 InitialAuthenticationFilter에 있음
    //컨트롤러 부분은 단순 User 정보 반환
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginRequest.UserLoginDTO userDTO = userLoginRequest.userLoginDTO();
        UserLoginResponse response = userService.getLoginUser(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest) {
        UserRegisterRequest.UserRegisterDTO userDTO = userRegisterRequest.userRegisterDTO();
        UserRegisterResponse response = userService.registerUser(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
