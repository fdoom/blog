package cyou.noteit.api.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDTO {
    @Positive
    private Long postId;

    @Positive
    private Long parentCommentId;

    @NotBlank(message = "댓글의 내용은 공백일 수 없습니다.")
    private String commentContent;
}
