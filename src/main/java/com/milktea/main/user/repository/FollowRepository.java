package com.milktea.main.user.repository;

import com.milktea.main.user.entity.Follow;
import com.milktea.main.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}
