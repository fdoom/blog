package cyou.noteit.api.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.domain.jwt.repository.RefreshTokenRepository;
import cyou.noteit.api.global.security.filter.CustomLogoutFilter;
import cyou.noteit.api.global.security.filter.JwtFilter;
import cyou.noteit.api.global.security.filter.LoginFilter;
import cyou.noteit.api.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, objectMapper, refreshTokenRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class)

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/account/join").permitAll()
                        .requestMatchers("/reissue").permitAll()
                        .anyRequest().authenticated()
                )

                .cors((cors) -> cors
                        .configurationSource((request) -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowCredentials(true);
                            configuration.setAllowedOrigins(Collections.singletonList("*"));
                            configuration.setAllowedHeaders(Collections.singletonList("*"));
                            configuration.setAllowedMethods(Collections.singletonList("*"));
                            configuration.setMaxAge(3600L);
                            configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                            return configuration;
                        })
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
