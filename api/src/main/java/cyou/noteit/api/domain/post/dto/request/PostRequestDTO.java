package cyou.noteit.api.domain.post.dto.request;

import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import lombok.Getter;

@Getter
public class PostRequestDTO {
    private String postTitle;
    private String postContent;
    private ShareStatus shareStatus;
    private Long categoryId;
}
