package cyou.noteit.api.domain.jwt.repository;

import cyou.noteit.api.domain.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Boolean existsByToken(String refresh);

    @Transactional
    void deleteByToken(String refresh);
}
