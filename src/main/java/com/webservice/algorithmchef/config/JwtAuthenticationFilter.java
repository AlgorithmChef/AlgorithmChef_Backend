package com.webservice.algorithmchef.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// (⭐수정) algorithmchef 프로젝트의 의존성으로 변경
import com.webservice.algorithmchef.service.UserService; 
import com.webservice.algorithmchef.util.JwtUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserService userService; // (UserService가 UserDetailsService를 구현해야 함)

    /**
     * 💡 임시 비밀번호(status="TEMPORARY") 사용자가 접근할 수 있는 유일한 경로.
     * 이 경로 외의 모든 API 접근은 403 Forbidden을 반환합니다.
     */
    private static final String ALLOWED_PATH_FOR_TEMP_USER = "/auth/findPassword";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("JwtAuthenticationFilter 실행: URI = {}", request.getRequestURI());

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 1. JWT 헤더가 없거나 'Bearer'가 아닌 경우
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization 헤더가 없거나 Bearer 타입이 아님");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        Claims claims = null;

        // 2. 토큰 유효성 검사 (만료, 변조 등)
        try {
            claims = jwtUtil.getClaims(token);
        } catch (Exception e) {
            log.warn("유효하지 않은 JWT 토큰: {}", e.getMessage());
            // (참고) 여기서 401 Unauthorized를 바로 반환할 수도 있으나, 
            //       SecurityConfig의 exceptionHandling에서 처리하도록 위임하는 것이 일반적입니다.
        }

        // 3. 토큰이 유효하고, 아직 SecurityContext에 인증 정보가 없는 경우
        if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // claims.getSubject()는 User 엔티티의 "userLoginId" (로그인 ID)여야 합니다.
            String userLoginId = claims.getSubject();
            String status = claims.get("status", String.class); // "status" 클레임 추출
            log.info("토큰 유효, userLoginId: {}, status: {}", userLoginId, status);

            // 4. '임시 비밀번호' 사용자인지 확인
            if ("TEMPORARY".equals(status)) {
                String requestURI = request.getRequestURI();
                log.info("임시 비밀번호 사용자 접근. 요청 URI: {}", requestURI);

                // 5. 허용된 URL이 아닌 경우 접근을 차단 (403 Forbidden)
                if (!ALLOWED_PATH_FOR_TEMP_USER.equals(requestURI)) {
                    log.warn("임시 비밀번호 사용자가 허용되지 않은 API에 접근 시도: {}", requestURI);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "비밀번호를 변경해야 합니다.");
                    return; // 필터 체인 중단
                }
            }
            
            // 6. (정상 사용자) 또는 (임시 사용자가 허용된 URL에 접근한 경우)
            //    UserDetails를 DB에서 조회하여 인증 토큰 생성
            UserDetails userDetails = userService.loadUserByUsername(userLoginId);
            
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 7. SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.info("SecurityContext에 인증 정보 저장 완료: {}", userLoginId);
        }
        
        // 8. 다음 필터로 체인 넘김
        filterChain.doFilter(request, response);
    }
}