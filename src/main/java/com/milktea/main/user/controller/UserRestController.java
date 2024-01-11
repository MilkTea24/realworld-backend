package com.milktea.main.user.controller;

import com.milktea.main.user.dto.UserLoginRequest;
import com.milktea.main.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PostMapping("/api/users/login")
    public ResponseEntity<?> login(@Valid UserLoginRequest user) {
        return new ResponseEntity<>(null, null);
    }


}
