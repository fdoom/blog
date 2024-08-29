package cyou.noteit.api.domain.jwt.service;

import cyou.noteit.api.global.redis.RedisUtil;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    private final static String ACCESS_TOKEN_NAME = "access";
    private final static Long ACCESS_TOKEN_EXPIRATION_MS = 600000L;

    private final static String REFRESH_TOKEN_NAME = "refresh";
    private final static Long REFRESH_TOKEN_EXPIRATION_MS = 86400000L;

    @Override
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookies(request)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        validateRefreshToken(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt(ACCESS_TOKEN_NAME, username, role, ACCESS_TOKEN_EXPIRATION_MS);
        String newRefreshToken = jwtUtil.createJwt(REFRESH_TOKEN_NAME, username, role, REFRESH_TOKEN_EXPIRATION_MS);
        addRefreshToken(username, newRefreshToken);

        setResponseHeaders(response, newAccessToken, newRefreshToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
        return Stream.of(Optional.ofNullable(request.getCookies())
                        .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)))
                .filter(cookie -> REFRESH_TOKEN_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private void validateRefreshToken(String refreshToken) {
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        String category = jwtUtil.getCategory(refreshToken);
        String username = jwtUtil.getUsername(refreshToken);
        if (
                !category.equals(REFRESH_TOKEN_NAME) ||
                !redisUtil.existsRefreshToken(username) ||
                !redisUtil.getRefreshToken(username).equals(refreshToken)
        ) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void setResponseHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie(refreshToken));
    }

    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie(ReissueServiceImpl.REFRESH_TOKEN_NAME, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setAttribute("sameSite", "None");
        return cookie;
    }

    private void addRefreshToken(String username, String refreshToken) {
        redisUtil.builder()
                .refreshToken(refreshToken)
                .username(username)
                .expirationMs(ReissueServiceImpl.REFRESH_TOKEN_EXPIRATION_MS)
                .build();
    }
}
