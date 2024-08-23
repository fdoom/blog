package cyou.noteit.api.domain.post.repository;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.category.entity.Category;
import cyou.noteit.api.domain.post.entity.Post;
import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByPostIdAndAccount(Long postId, Account account);

    Optional<Post> findByPostIdAndShareStatus(Long postId, ShareStatus shareStatus);

    List<Post> findAllByShareStatus(ShareStatus shareStatus);

    List<Post> findAllByShareStatusAndCategory(ShareStatus shareStatus, Category category);
}
