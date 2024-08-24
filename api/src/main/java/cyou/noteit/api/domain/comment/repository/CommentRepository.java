package cyou.noteit.api.domain.comment.repository;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.comment.entity.Comment;
import cyou.noteit.api.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostAndParentCommentIsNull(Post post);

    Optional<Comment> findByCommentIdAndAccount(Long commentId, Account account);
}
