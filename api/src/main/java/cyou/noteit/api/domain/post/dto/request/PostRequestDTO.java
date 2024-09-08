package cyou.noteit.api.domain.post.dto.request;

import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDTO {
    private String postTitle;
    private String postContent;
    private ShareStatus shareStatus;
    private Long categoryId;
}
