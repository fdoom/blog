package cyou.noteit.api.domain.comment.dto.request;

import lombok.Getter;

@Getter
public class CommentRequestDTO {
    private Long postId;
    private Long parentCommentId;
    private String commentContent;
}