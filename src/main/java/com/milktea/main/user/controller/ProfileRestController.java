package com.milktea.main.user.controller;

import com.milktea.main.user.dto.response.ProfileInfoResponse;
import com.milktea.main.user.service.ProfileService;
import com.milktea.main.util.security.jwt.JwtTokenAdministrator;
import com.milktea.main.util.security.jwt.JwtTokenBlackList;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileRestController {
    private final ProfileService profileService;
    @GetMapping("/api/profiles/{username}")
    public ResponseEntity<?> getProfileInfo(@PathVariable String username, HttpServletRequest request) {
        String token = request.getHeader(JwtTokenAdministrator.TOKEN_HEADER_NAME);
        ProfileInfoResponse response = profileService.getProfileInfo(username, token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
