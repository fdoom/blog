package cyou.noteit.api.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.global.config.security.filter.CustomErrorFilter;
import cyou.noteit.api.global.config.security.service.CustomUserDetailsService;
import cyou.noteit.api.global.redis.RedisUtil;
import cyou.noteit.api.global.config.security.filter.CustomLogoutFilter;
import cyou.noteit.api.global.config.security.filter.JwtFilter;
import cyou.noteit.api.global.config.security.filter.LoginFilter;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomErrorFilter customErrorFilter;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${ALLOWED_ORIGIN_PATTERNS}")
    private String ALLOWED_ORIGIN_PATTERNS;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(
                        LoginFilter.builder()
                                .authenticationManager(authenticationManager(authenticationConfiguration))
                                .objectMapper(objectMapper)
                                .redisUtil(redisUtil)
                                .jwtUtil(jwtUtil)
                                .build(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        JwtFilter.builder()
                                .jwtUtil(jwtUtil)
                                .customUserDetailsService(customUserDetailsService)
                                .build(),
                        LoginFilter.class
                )
                .addFilterBefore(
                        CustomLogoutFilter.builder()
                                .jwtUtil(jwtUtil)
                                .redisUtil(redisUtil)
                                .build(),
                        LogoutFilter.class
                )
                .addFilterBefore(customErrorFilter, JwtFilter.class)
                .addFilterBefore(customErrorFilter, CustomLogoutFilter.class)

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/docs/index.html").permitAll()
                        .requestMatchers("/account/join","/reissue").permitAll()

                        .requestMatchers(HttpMethod.GET, "/category/info/**", "/category/search/**").permitAll()
                        .requestMatchers("/category/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/post/info/**", "/post/search/**").permitAll()
                        .requestMatchers("/post/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/comment/info/**").permitAll()

                        .requestMatchers("/image/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
