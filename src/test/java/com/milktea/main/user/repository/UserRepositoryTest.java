package com.milktea.main.user.repository;

import com.milktea.main.factory.UserMother;
import com.milktea.main.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //테스트 메서드마다 데이터베이스 초기화
@Slf4j
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private static User user;

    @BeforeEach
    void setup() {
        user = UserMother.user().build();
    }
    @Test
    @DisplayName("save 테스트")
    public void user_save_success_test() {
        //given
        //when
        User dbUser = userRepository.save(user);

        //then
        Assertions.assertEquals(1L, dbUser.getId());
    }

    @Test
    @DisplayName("findByUsername 성공 테스트")
    public void user_findByUsername_success_test() {
        //given
        userRepository.save(user);

        //when
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        //then
        Assertions.assertTrue(optionalUser.isPresent());
        Assertions.assertEquals(1L, optionalUser.get().getId());
    }

    @Test
    @DisplayName("findByUsername 실패 테스트")
    public void user_findByUsername_fail_test() {
        //given

        //when
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        //then
        Assertions.assertTrue(optionalUser.isEmpty());
    }
}
