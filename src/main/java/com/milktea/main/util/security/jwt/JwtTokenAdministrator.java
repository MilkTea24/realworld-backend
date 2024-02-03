package com.milktea.main.util.security.jwt;

import com.milktea.main.util.exceptions.ExceptionUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenAdministrator {
    private static final long TOKEN_EXPIRATION_SECONDS = 3600L;

    private static final String AUTHORITY_DELIMITER = ",";

    @Value("${jwt.signing.key}")
    private final String signingKey;

    public static String authorityDelimiter() {
        return AUTHORITY_DELIMITER;
    }

    public String issueToken(String email, Authentication returnAuthentication){
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );

        log.debug("AuthenticationManager가 반환한 username - {}", returnAuthentication.getName());
        log.debug("AuthenticationManager가 반환한 authorities - {}", returnAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).reduce((a, b) -> a + b));

        //만료 기한 추가
        Instant expiredInstant = Instant.now().plusSeconds(1800L);
        Claims claims = Jwts.claims()
                .setExpiration(Date.from(expiredInstant));

        claims.put("email", email);
        Collection<? extends GrantedAuthority> grantedAuthorities = returnAuthentication.getAuthorities();

        claims.put("authorities",
                String.join(AUTHORITY_DELIMITER,
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .toList()
                )
        );

        String jwt = Jwts.builder()
                .setClaims(claims) //JWT와 관련된 정보 입력
                .signWith(key) //서명 키는 실제로 사용자별로 다른 키를 사용해야 한다.
                .compact();

        return jwt;
    }

    public Claims verifyToken(String jwt) throws ServletException {
        String parsingJwt = jwt.replaceFirst("Token ", "");

        //서명된 Jwt를 풀기 위한 Secret Key 생성
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );

        return parseJwtToken(key, parsingJwt);
    }

    public Claims parseJwtToken(SecretKey key, String parsingJwt) throws ServletException {
        //JWT에서 정보 얻기(모든 예외는 여기서 로그만 남기고 ExceptionHandlingFilter가 UNAUTHORIZED로 처리할 것)
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key) //서명 검증을 위한 SecretKey 입력
                    .build()
                    .parseClaimsJws(parsingJwt) //토큰이 유효한지 검사. 유효하지 않으면 여러 종류 예외 발생
                    .getBody();
        }
        catch (ExpiredJwtException e) {
            log.warn("Jwt token이 만료되어 접근할 수 없음!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new ServletException("로그인 시간이 만료되었습니다. 다시 로그인해주세요.");
        }
        catch (SignatureException e) {
            log.warn("유효하지 않은 인증으로 접근 시도!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new ServletException("인증에 실패하였습니다. 다시 로그인해주세요.");
        }
        catch (MalformedJwtException | UnsupportedJwtException e) {
            log.warn("잘못된 jwt 형식 받음!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new ServletException("인증에 실패하였습니다. 다시 로그인해주세요.");
        }
        catch (Exception e) {
            log.warn("jwt 파싱 과정에서 예외 발생!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new ServletException("인증에 실패하였습니다. 다시 로그인해주세요.");
        }

        return claims;
    }

    public String updateToken(String email, String token) {

    }
}