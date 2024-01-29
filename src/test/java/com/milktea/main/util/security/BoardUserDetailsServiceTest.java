package com.milktea.main.util.security;

import com.milktea.main.factory.UserMother;
import com.milktea.main.user.entity.User;
import com.milktea.main.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Slf4j
public class BoardUserDetailsServiceTest {
    private static User correctTestUser;

    private static UserRepository userRepository;

    @BeforeEach
    void setup(){
        correctTestUser = UserMother.user().build();
        userRepository = Mockito.mock(UserRepository.class);
    }

    @Test
    @DisplayName("email이 존재하면 BoardUserDetails를 반환한다.")
    void correct_username_test() {
        //given
        BoardUserDetailsService boardUserDetailsService = new BoardUserDetailsService(userRepository);
        String testEmail = correctTestUser.getEmail();

        //when
        when(userRepository.findByEmail(eq("newUser@naver.com"))).thenReturn(Optional.of(correctTestUser));
        BoardUserDetails result = boardUserDetailsService.loadUserByUsername(testEmail);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("newUser@naver.com", result.getUsername());
    }

    @Test
    @DisplayName("존재하지 않는 email이 주어지면 UsernameNotFoundException이 발생한다.")
    void incorrect_username_test() {
        //given
        BoardUserDetailsService boardUserDetailsService = new BoardUserDetailsService(userRepository);
        String testEmail = correctTestUser.getEmail();

        //when
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class,() -> boardUserDetailsService.loadUserByUsername(testEmail));
    }
}
