package com.milktea.main.user.repository;

import com.milktea.main.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"authorities"})
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
