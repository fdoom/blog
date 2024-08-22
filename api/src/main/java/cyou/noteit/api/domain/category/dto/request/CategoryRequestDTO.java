package cyou.noteit.api.domain.category.dto.request;

import lombok.Getter;

@Getter
public class CategoryRequestDTO {
    private String categoryName;
    private Long parentCategoryId;
}
