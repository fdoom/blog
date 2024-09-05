package cyou.noteit.api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {
    @Value("${ALLOWED_ORIGIN_PATTERNS}")
    private String ALLOWED_ORIGIN_PATTERNS;

    @Value("${TEST_ORIGIN_PATTERNS}")
    private String TEST_ORIGIN_PATTERNS;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS, TEST_ORIGIN_PATTERNS)
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*")
                .allowedMethods("*")
                .exposedHeaders("Authorization");
    }
}
