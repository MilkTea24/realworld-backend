package com.milktea.main.util.security.jwt;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtTokenBlackListRepository extends CrudRepository<JwtTokenBlackList, String> {
}
