package cyou.noteit.api.global.config.security.filter;


import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import cyou.noteit.api.global.config.security.service.CustomUserDetailsService;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Builder
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";
    private static final String ACCESS_TOKEN_BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_NAME = "access";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 Bearer 토큰 추출
        String authHeader = request.getHeader(ACCESS_TOKEN_HEADER_NAME);
        if (authHeader == null || !authHeader.startsWith(ACCESS_TOKEN_BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.substring(ACCESS_TOKEN_BEARER_PREFIX.length());

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals(ACCESS_TOKEN_NAME)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        String username = jwtUtil.getUsername(accessToken);
        UserDetails customUserDetails = customUserDetailsService.loadUserByUsername(username);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}