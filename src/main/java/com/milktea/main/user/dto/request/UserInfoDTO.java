package com.milktea.main.user.dto.request;

import com.google.gson.annotations.SerializedName;
import com.milktea.main.user.entity.User;
import lombok.extern.slf4j.Slf4j;

//GET 매핑 DTO
@Slf4j
public record UserInfoDTO(
            String email
) {
        public UserInfoDTO{
            log.debug("UserInfoDTO 생성 - email = {}", email);
        }

        //테스트 데이터 생성용
        public UserInfoDTO(User user) {
            this(user.getEmail());
        }
}