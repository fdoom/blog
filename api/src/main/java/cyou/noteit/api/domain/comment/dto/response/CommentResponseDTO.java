package cyou.noteit.api.domain.comment.dto.response;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDTO extends BaseEntityDTO {
    private Long commentId;
    private String commentContent;

    @Setter
    private String username;

    @Setter
    private List<CommentResponseDTO> childComments;

    @Builder
    CommentResponseDTO(Long commentId, String commentContent, String username, List<CommentResponseDTO> childComments, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.username = username;
        this.childComments = childComments;
    }
}
