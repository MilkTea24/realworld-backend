package com.milktea.main.factory;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockSecurityContextFactory.class)
public @interface WithCustomUser {
    String email() default "";
    String password() default "";

    String bio() default "";

    String image() default "";
    String[] authority() default {};
}
