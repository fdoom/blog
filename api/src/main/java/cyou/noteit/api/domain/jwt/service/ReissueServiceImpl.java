package cyou.noteit.api.domain.jwt.service;

import cyou.noteit.api.domain.jwt.entity.RefreshToken;
import cyou.noteit.api.domain.jwt.repository.RefreshTokenRepository;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import cyou.noteit.api.global.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookies(request)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        validateRefreshToken(refreshToken);

        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);

        refreshTokenRepository.deleteByToken(refreshToken);
        addRefreshEntity(username, newRefreshToken, 86400000L);

        setResponseHeaders(response, newAccessToken, newRefreshToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Optional<String> getRefreshTokenFromCookies(HttpServletRequest request) {
        return Stream.of(Optional.ofNullable(request.getCookies())
                        .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)))
                .filter(cookie -> "refresh".equals(cookie.getName()))
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
        if (!category.equals("refresh") || !refreshTokenRepository.existsByToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void setResponseHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie("refresh", refreshToken));
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String username, String refreshToken, Long expirationMs) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationMs);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .username(username)
                .token(refreshToken)
                .expiration(expirationDate.toString())
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }
}
