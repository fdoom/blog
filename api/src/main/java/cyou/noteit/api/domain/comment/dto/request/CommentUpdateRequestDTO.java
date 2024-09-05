package cyou.noteit.api.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateRequestDTO {
    @NotBlank
    private String commentContent;
}
