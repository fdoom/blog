package cyou.noteit.api.domain.jwt.controller;

import cyou.noteit.api.domain.jwt.entity.RefreshToken;
import cyou.noteit.api.domain.jwt.repository.RefreshTokenRepository;
import cyou.noteit.api.domain.jwt.service.ReissueService;
import cyou.noteit.api.global.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private final JwtUtil jwtUtil;
    private final ReissueService reissueService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissue(request, response);
    }
}
