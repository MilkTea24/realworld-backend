package com.milktea.main.user.controller;

import com.milktea.main.user.dto.UserLoginRequest;
import com.milktea.main.user.dto.UserRegisterRequest;
import com.milktea.main.user.dto.UserRegisterResponse;
import com.milktea.main.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid UserLoginRequest user) {
        return new ResponseEntity<>(null, null);
    }

    @PostMapping("/")
    public ResponseEntity<?> register(@Valid UserRegisterRequest userRegisterRequest) {
        UserRegisterResponse response = userService.registerUser(userRegisterRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
