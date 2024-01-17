package com.milktea.main.factory;

import com.milktea.main.user.entity.Authority;
import com.milktea.main.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMother {
    public static UserBuilder user() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private String email = "newUser@naver.com";
        private String username = "newUser";
        private String bio = "bio1";
        private String image = null;
        private String password = "12341234";
        private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        private List<Authority> authorities = List.of(AuthorityMother.authority().build());

        public UserBuilder withPassword(String password, PasswordEncoder passwordEncoder) {
            this.password = password;
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public UserBuilder withAuthorities(List<Authority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public User build() {
            User returnUser = new User(
                    email,
                    username,
                    bio,
                    image
            );

            //password 설정
            returnUser.setPassword(password, passwordEncoder);

            //authority 설정
            for (Authority a : authorities) {
                returnUser.addAuthority(a);
            }

            return returnUser;
        }
    }
}
