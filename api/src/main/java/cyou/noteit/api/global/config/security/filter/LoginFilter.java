package cyou.noteit.api.global.config.security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import cyou.noteit.api.global.exception.ErrorResponse;
import cyou.noteit.api.global.redis.RedisUtil;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


@RequiredArgsConstructor
@Builder
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;

    private static final String ACCESS_TOKEN_NAME = "access";
    private static final Long ACCESS_TOKEN_EXPIRATION_MS = 600000L;

    private static final String REFRESH_TOKEN_NAME = "refresh";
    private static final Long REFRESH_TOKEN_EXPIRATION_MS = 86400000L;

    // 로그인 필터
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Map<String, String> requestBody = null;
        try {
            requestBody = objectMapper.readValue(request.getInputStream(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new CustomException(ErrorCode.LOGIN_REQUEST_BODY_NOT_FOUND);
        }
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        // 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        // 토큰 검증 과정 진행
        return authenticationManager.authenticate(authToken);
    }

    // 토큰 검증 성공 시 필터
    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = jwtUtil.createJwt(ACCESS_TOKEN_NAME, username, role, ACCESS_TOKEN_EXPIRATION_MS);
        String refresh = jwtUtil.createJwt(REFRESH_TOKEN_NAME, username, role, REFRESH_TOKEN_EXPIRATION_MS);

        addRefreshToken(username, refresh);

        response.setHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie(refresh));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addRefreshToken(String username, String refresh) {
        redisUtil.builder()
                .username(username)
                .refreshToken(refresh)
                .expirationMs(REFRESH_TOKEN_EXPIRATION_MS)
                .build();
    }

    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie(LoginFilter.REFRESH_TOKEN_NAME, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    // 토큰 검증 실패 시 필터
    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED)
                        .message(failed.getMessage())
                        .build()
        ));

    }
}
