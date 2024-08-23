package cyou.noteit.api.domain.post.dto.response;

import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.Getter;

@Getter
public class PostResponseDTO extends BaseEntityDTO {
    private Long postId;
    private String postTitle;
    private String postContent;
    private ShareStatus shareStatus;
}
