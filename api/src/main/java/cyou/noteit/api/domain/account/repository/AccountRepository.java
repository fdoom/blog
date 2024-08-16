package cyou.noteit.api.domain.account.repository;

import cyou.noteit.api.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Boolean existsByUsername(String username);

    Optional<Account> findByUsername(String username);
}
