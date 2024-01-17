package com.milktea.main.factory;

import com.milktea.main.user.entity.Authority;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorityMother {
    public static AuthorityBuilder authority() {
        return new AuthorityBuilder();
    }

    public static class AuthorityBuilder {
        private String name = "USER";

        public AuthorityBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public Authority build() {
            return new Authority(name);
        }
    }
}
