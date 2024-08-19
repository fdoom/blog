package cyou.noteit.api.global.config.security.filter;


import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import cyou.noteit.api.global.redis.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Builder
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilter {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private static final String LOGOUT_URI = "/logout";
    private static final String REFRESH_TOKEN_NAME = "refresh";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = getRefreshTokenFromCookies(request)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!isValidRefreshToken(refreshToken))
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);

        // 로그아웃 처리
        processLogout(response, refreshToken);
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return LOGOUT_URI.equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
    }

    private Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
        return Stream.of(Optional.ofNullable(request.getCookies())
                        .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)))
                .filter(cookie -> REFRESH_TOKEN_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private boolean isValidRefreshToken(String refreshToken) {
        String category = jwtUtil.getCategory(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        if (!REFRESH_TOKEN_NAME.equals(category)) {
            return false;
        }

        return redisUtil.existsRefreshToken(username);
    }

    private void processLogout(HttpServletResponse response, String refreshToken) {
        String username = jwtUtil.getUsername(refreshToken);
        redisUtil.deleteRefreshToken(username);

        Cookie expiredCookie = new Cookie(REFRESH_TOKEN_NAME, null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");

        response.addCookie(expiredCookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
