package cyou.noteit.api.global.redis;

import lombok.Builder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Builder
    public void setRefreshToken(String username, String refreshToken, long expirationMs) {
        redisTemplate.opsForValue().set(username, refreshToken, Duration.ofMillis(expirationMs));
    }

    public boolean existsRefreshToken(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(username));
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get(username);
    }

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }
}
