package com.webservice.algorithmchef.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret.key}")
    private String secretKey;

    // Access Token 유효 시간 (1시간)
    private final long accessTokenValidityInMilliseconds = 60 * 60 * 1000L;
    // Refresh Token 유효 시간 (7일)
    private final long refreshTokenValidityInMilliseconds = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Secret Key를 기반으로 HMAC-SHA 키를 생성합니다.
     */
    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 사용자 정보를 기반으로 Access Token을 생성합니다.
     * @param userId      사용자 식별자 (토큰의 주체, 예: userLoginId)
     * @param roles       사용자의 역할 목록
     * @param isTemporary 임시 비밀번호 사용 여부 (또는 다른 상태)
     * @return 생성된 Access Token
     */
    public String createAccessToken(String userId, String role, boolean isTemporary) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role);
        claims.put("status", isTemporary ? "TEMPORARY" : "ACTIVE");

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 사용자 정보를 기반으로 Refresh Token을 생성합니다.
     * Refresh Token에는 민감한 정보(roles, status 등)를 포함하지 않는 것이 일반적입니다.
     * @param userId 사용자 식별자 (토큰의 주체)
     * @return 생성된 Refresh Token
     */
    public String createRefreshToken(String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 모든 클레임(정보)을 추출하는 중앙 메서드입니다.
     * @param token 정보룰 추출할 토큰
     * @return 토큰의 Claims 객체
     * @throws ExpiredJwtException, MalformedJwtException 등 모든 JWT 예외
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에서 Subject(사용자 식별자)를 추출합니다.
     * @param token 검증할 JWT
     * @return 추출된 Subject
     */
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * @param token 검증할 JWT
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}