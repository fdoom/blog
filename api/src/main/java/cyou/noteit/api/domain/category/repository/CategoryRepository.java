package cyou.noteit.api.domain.category.repository;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByAccountAndCategoryName(Account account, String categoryName);

    List<Category> findByAccountAndParentCategoryIsNull(Account account);

    Optional<Category> findByAccountAndCategoryId(Account account, Long categoryId);

    Boolean existsByAccountAndCategoryNameAndCategoryIdNot(Account account, String categoryName, Long categoryId);
}
