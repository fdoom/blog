package cyou.noteit.api.domain.comment.repository;

import cyou.noteit.api.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
