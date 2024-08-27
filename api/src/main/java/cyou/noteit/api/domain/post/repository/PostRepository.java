package cyou.noteit.api.domain.post.repository;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPostIdAndAccount(Long postId, Account account);

    @Query(value = """
    SELECT *
    FROM post
    WHERE post_id = :postId
    AND (
        CASE 
            WHEN :shareStatus = 'PRIVATE' THEN share_status IN ('PRIVATE', 'PROTECTED', 'PUBLIC')
            WHEN :shareStatus = 'PROTECTED' THEN share_status IN ('PROTECTED', 'PUBLIC')
            ELSE share_status = 'PUBLIC'
        END
    )
    """, nativeQuery = true)
    Optional<Post> findByPostIdAndShareStatus(@Param("postId") Long postId, @Param("shareStatus") String shareStatus);

    @Query(value = """
    SELECT *
    FROM post
    WHERE 
    (
        CASE 
            WHEN :shareStatus = 'PRIVATE' THEN share_status IN ('PRIVATE', 'PROTECTED', 'PUBLIC')
            WHEN :shareStatus = 'PROTECTED' THEN share_status IN ('PROTECTED', 'PUBLIC')
            ELSE share_status = 'PUBLIC'
        END
    )
    """, nativeQuery = true)
    List<Post> findAllByShareStatus(@Param("shareStatus") String shareStatus, Pageable pageable);

    @Query(value = """
    SELECT *
    FROM post
    WHERE post.category_id = :categoryId
    AND (
        CASE 
            WHEN :shareStatus = 'PRIVATE' THEN share_status IN ('PRIVATE', 'PROTECTED', 'PUBLIC')
            WHEN :shareStatus = 'PROTECTED' THEN share_status IN ('PROTECTED', 'PUBLIC')
            ELSE share_status = 'PUBLIC'
        END
    )
    """, nativeQuery = true)
    List<Post> findAllByShareStatusAndCategory(@Param("shareStatus") String shareStatus, @Param("categoryId") Long categoryId, Pageable pageable);

    @Query(value = """
    SELECT *
    FROM post
    WHERE MATCH(post_title)
    AGAINST(:postTitle IN NATURAL LANGUAGE MODE)
    AND (
        CASE 
            WHEN :shareStatus = 'PRIVATE' THEN share_status IN ('PRIVATE', 'PROTECTED', 'PUBLIC')
            WHEN :shareStatus = 'PROTECTED' THEN share_status IN ('PROTECTED', 'PUBLIC')
            ELSE share_status = 'PUBLIC'
        END
    )
    """, nativeQuery = true)
    List<Post> findAllByPostTitle(@Param("postTitle") String postTitle, @Param("shareStatus") String shareStatus, Pageable pageable);
}
