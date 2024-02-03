package com.milktea.main.util.security.jwt;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "tokenBlackList", timeToLive = 3600L) //timeToLive는 토큰의 만료시간과 동일하게
public class JwtTokenBlackList {
    @Id
    private String email; //hash의 key가 됨(전체 키 값 -> RedisHash의 value + id의 값)

    private String tokenValue;

    public JwtTokenBlackList(String email, String tokenValue) {
        this.email = email;
        this.tokenValue = tokenValue;
    }
}
