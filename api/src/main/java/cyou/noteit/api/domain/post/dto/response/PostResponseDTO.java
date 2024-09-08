package cyou.noteit.api.domain.post.dto.response;

import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO extends BaseEntityDTO {
    private Long postId;
    private String postTitle;
    private String postContent;
    private ShareStatus shareStatus;

    @Builder
    PostResponseDTO(Long postId, String postTitle, String postContent, ShareStatus shareStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.shareStatus = shareStatus;
    }
}
