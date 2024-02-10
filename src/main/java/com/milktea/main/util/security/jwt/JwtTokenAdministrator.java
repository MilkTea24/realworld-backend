package com.milktea.main.util.security.jwt;

import com.milktea.main.util.exceptions.ExceptionUtils;
import com.milktea.main.util.exceptions.JwtAuthenticationException;
import com.milktea.main.util.security.EmailPasswordAuthentication;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenAdministrator {
    private static final long TOKEN_EXPIRATION_SECONDS = 3600L;

    private static final String AUTHORITY_DELIMITER = ",";

    @Value("${jwt.signing.key}")
    private final String signingKey;

    private final JwtTokenBlackListRepository jwtTokenBlackListRepository;

    private final Clock clock;


    public static String authorityDelimiter() {
        return AUTHORITY_DELIMITER;
    }

    public String issueToken(Authentication returnAuthentication){
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );

        log.debug("AuthenticationManager가 반환한 username - {}", returnAuthentication.getName());
        log.debug("AuthenticationManager가 반환한 authorities - {}", returnAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).reduce((a, b) -> a + b));

        //만료 기한 추가
        Instant expiredInstant = Instant.now(clock).plusSeconds(1800L);
        Claims claims = Jwts.claims()
                .setExpiration(Date.from(expiredInstant));

        String email = returnAuthentication.getName();
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

        return "Token " + jwt;
    }

    public Claims verifyToken(String jwt) throws ServletException {
        log.debug("현재 시간 -{}", clock);
        String parsingJwt = jwt.replaceFirst("Token ", "");

        //서명된 Jwt를 풀기 위한 Secret Key 생성
        SecretKey key = Keys.hmacShaKeyFor(
                signingKey.getBytes(StandardCharsets.UTF_8)
        );

        Claims claims = parseJwtToken(key, parsingJwt);

        //만약 블랙리스트에 포함된 토큰이라면 예외 발생시키기
        String email = claims.get("email", String.class);
        Optional<JwtTokenBlackList> blackListTokenOp = jwtTokenBlackListRepository.findById(email);
        //블랙리스트에 없으면 검증 완료
        if (blackListTokenOp.isEmpty()) return claims;

        //블랙리스트에 있으면
        String blackListToken = blackListTokenOp.get().getTokenValue();
        if (blackListToken.equals(parsingJwt)) throw new BadCredentialsException("유효하지 않은 토큰입니다.");
        else return claims;
    }

    public String updateToken(Authentication auth, String newEmail, String oldJwt) {
        //이메일 변경 시 기존 토큰 만료시키기
        String oldEmail = auth.getName();

        String parsingOldJwt = oldJwt.replaceFirst("Token ", "");
        if (!emailCanChange()) throw new RuntimeException("현재 이메일을 변경할 수 없습니다.");
        jwtTokenBlackListRepository.save(new JwtTokenBlackList(oldEmail, parsingOldJwt));

        //새 토큰 발급
        return issueToken(new EmailPasswordAuthentication(newEmail, null, auth.getAuthorities()));
    }

    //이메일을 너무 자주 발급받아 블랙리스트에 토큰이 계속 갱신되는 것을 막기 위해 이메일 변경 기간을 둠
    //토큰 만료 기한(redis ttl 기한)보다 이메일 변경 가능 기한을 훨씬 길게 설정
    private boolean emailCanChange() {
        //리팩토링 필요
        return true;
    }

    private Claims parseJwtToken(SecretKey key, String parsingJwt) {
        //JWT에서 정보 얻기(모든 예외는 여기서 로그만 남기고 ExceptionHandlingFilter가 UNAUTHORIZED로 처리할 것)
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key) //서명 검증을 위한 SecretKey 입력
                    .setClock(() -> Date.from(clock.instant()))
                    .build()
                    .parseClaimsJws(parsingJwt) //토큰이 유효한지 검사. 유효하지 않으면 여러 종류 예외 발생
                    .getBody();


        }
        catch (ExpiredJwtException e) {
            log.warn("Jwt token이 만료되어 접근할 수 없음!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new JwtAuthenticationException("로그인 시간이 만료되었습니다. 다시 로그인해주세요.", e);
        }
        catch (SignatureException e) {
            log.warn("유효하지 않은 인증으로 접근 시도!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new JwtAuthenticationException("인증에 실패하였습니다. 다시 로그인해주세요.", e);
        }
        catch (MalformedJwtException | UnsupportedJwtException e) {
            log.warn("잘못된 jwt 형식 받음!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new JwtAuthenticationException("인증에 실패하였습니다. 다시 로그인해주세요.", e);
        }
        catch (Exception e) {
            log.warn("jwt 파싱 과정에서 예외 발생!");
            if (log.isDebugEnabled()) log.debug(ExceptionUtils.getStackTrace(e));
            throw new JwtAuthenticationException("인증에 실패하였습니다. 다시 로그인해주세요.", e);
        }

        return claims;
    }


}
