package cyou.noteit.api.domain.category.repository;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByCategoryName(String categoryName);

    List<Category> findByParentCategoryIsNullOrderByCategoryName();

    Boolean existsByCategoryNameAndCategoryIdNot(String categoryName, Long categoryId);

    @Query(value = """
        SELECT *
        FROM category
        WHERE MATCH(category_name)
        AGAINST(:categoryName IN NATURAL LANGUAGE MODE)
    """, nativeQuery = true)
    List<Category> findByCategoryName(@Param("categoryName") String categoryName);


    @Query("""
    SELECT c
    FROM Category c
    JOIN c.posts p
    WHERE p.postId = :postId
    """)
    Optional<Category> findCategoryByPostId(Long postId);

    List<Category> findAllByOrderByCategoryNameAsc();
}
