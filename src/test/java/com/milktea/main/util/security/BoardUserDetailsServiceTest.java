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
    @DisplayName("username이 존재하면 BoardUserDetails를 반환한다.")
    void correct_username_test() {
        //given
        BoardUserDetailsService boardUserDetailsService = new BoardUserDetailsService(userRepository);
        String testUsername = correctTestUser.getUsername();

        //when
        when(userRepository.findByUsername(eq(testUsername))).thenReturn(Optional.of(correctTestUser));
        BoardUserDetails result = boardUserDetailsService.loadUserByUsername(testUsername);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("newUser", result.getUsername());
    }

    @Test
    @DisplayName("존재하지 않는 username이 주어지면 UsernameNotFoundException이 발생한다.")
    void incorrect_username_test() {
        //given
        BoardUserDetailsService boardUserDetailsService = new BoardUserDetailsService(userRepository);
        String testUsername = correctTestUser.getUsername();

        //when
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(UsernameNotFoundException.class,() -> boardUserDetailsService.loadUserByUsername(testUsername));
    }
}
