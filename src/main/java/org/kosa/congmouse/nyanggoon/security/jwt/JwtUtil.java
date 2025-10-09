package org.kosa.congmouse.nyanggoon.security.jwt;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// Jwt 토큰 생성 및 검증 유틸리티
@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret)
    {
        // 문자열 비밀 키를 SecretKey 객체로 변환
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        log.info("JWT 비밀키 초기화 완료");
    }

    /**
     * JWT 토큰 생성
     * @param member 회원 정보
     * @param expiredMs 만료시간(ms)
     * @return 생성된 JWT 토큰 문자열
     */
    public String createJwt(Member member, Long expiredMs){
        log.info("JWT 토큰 생성 시작: username={}", member.getEmail());

        // 현재 시간
        Date now = new Date(System.currentTimeMillis());

        // 만료 시간 계산
        Date expiration = new Date(System.currentTimeMillis() + expiredMs);

        // JWT 토큰 생성
        String token = Jwts.builder()
                // Payload (Claims) 설정
                .claim("id", member.getId()) // 회원번호 (pk)
                .claim("email", member.getEmail()) // 로그인 이메일
                .claim("nickname", member.getNickname()) // 사용자 닉네임
                .claim("role", member.getRole().name()) //권한 (ROLE_USER, ROLE_ADMIN)

                // 토큰발급시간
                .issuedAt(now)
                // 토큰만료시간
                .expiration(expiration)

                // 서명
                .signWith(secretKey)

                // 토큰 생성
                .compact();

        log.info("JWT 토큰 생성 완료: 만료시간={}ms", expiredMs);
        return token;
    }

    /**
     * 토큰에서 회원 ID(PK) 추출
     * @param token
     * @return
     */
    public Long getId(String token){
        Long id = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
        log.debug("토큰에서 ID 추출: {}", id);
        return id;
    }

    /**
     * 토큰에서 username(로그인 ID) 추출
     * @param token
     * @return
     */
    public String getEmail(String token){
        String email = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
        log.debug("토큰에서 email 추출: {}", email);
        return email;
    }

    /**
     * 토큰에서 사용자 닉네임 추출
     * @param token
     * @return 닉네임
     */
    public String getNickname(String token){
        String name = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("name", String.class);

        log.debug("토큰에서 name 추출: {}", name);
        return name;
    }

    public String getRole(String token){
        String role = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);

        log.debug("토큰에서 role 추출: {}", role);
        return role;
    }

    /**
     * 토큰 만료 여부 검증
     * @param token
     * @return
     */
    public Boolean isExpired(String token){
        try{
            Date expiration = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            boolean expired = expiration.before(new Date());

            if(expired){
                log.warn("토큰이 완료됨: 만료시간={}", expiration);
            }

            return expired;
        } catch(Exception e){
            log.error("토큰 만료 검증 실패: {}", e.getMessage());
            return true; // 검증 실패 시 만료된 것으로 처리
        }
    }

    /**
     * 토큰 유효성 전체 검증
     * 서명 검증 + 만료 시간 체
     * @param token
     * @return
     */
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return !isExpired(token);
        } catch (Exception e){
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}
