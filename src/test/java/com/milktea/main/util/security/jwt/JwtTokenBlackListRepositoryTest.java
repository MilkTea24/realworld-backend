package com.milktea.main.util.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtTokenBlackListRepositoryTest {
    @Autowired
    private JwtTokenBlackListRepository jwtTokenBlackListRepository;

    private static JwtTokenBlackList blackList;

    @BeforeEach
    void setup() {
        blackList = new JwtTokenBlackList("newUser@naver.com", "jwtToken");
    }
    //Embedded Redis를 실행하기 전에 Redis를 서비스 종료해야함! -> 포트 충돌
    @Test
    @DisplayName("save 테스트")
    public void blacklist_save_success_test() {
        //given
        //when
        JwtTokenBlackList jwtTokenBlackList = jwtTokenBlackListRepository.save(blackList);

        //then
        Assertions.assertEquals("jwtToken", jwtTokenBlackList.getTokenValue());
    }

    @Test
    @DisplayName("findById 성공 테스트")
    public void blacklist_findById_success_test() {
        //given
        jwtTokenBlackListRepository.save(blackList);

        //when
        Optional<JwtTokenBlackList> optionalBlackList = jwtTokenBlackListRepository.findById("newUser@naver.com");

        //then
        Assertions.assertTrue(optionalBlackList.isPresent());
        Assertions.assertEquals("jwtToken", optionalBlackList.get().getTokenValue());
    }

    @Test
    @DisplayName("findByUsername 실패 테스트")
    public void user_findByUsername_fail_test() {
        //given

        //when
        Optional<JwtTokenBlackList> optionalBlackList = jwtTokenBlackListRepository.findById("newUser@naver.com");

        //then
        Assertions.assertTrue(optionalBlackList.isEmpty());
    }
}