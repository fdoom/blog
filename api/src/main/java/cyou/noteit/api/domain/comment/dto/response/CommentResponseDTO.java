package cyou.noteit.api.domain.comment.dto.response;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class CommentResponseDTO extends BaseEntityDTO {
    private Long commentId;
    private String commentContent;

    @Setter
    private String username;

    @Setter
    private List<CommentResponseDTO> childComments;
}
