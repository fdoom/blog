package cyou.noteit.api.global.security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.domain.jwt.entity.RefreshToken;
import cyou.noteit.api.domain.jwt.repository.RefreshTokenRepository;
import cyou.noteit.api.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인 필터
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Map<String, String> requestBody = null;
        try {
            requestBody = objectMapper.readValue(request.getInputStream(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
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

        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        addRefreshEntity(username, refresh, 86400000L);

        response.setHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(username)
                .token(refresh)
                .expiration(date.toString())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    // 토큰 검증 실패 시 필터
    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
