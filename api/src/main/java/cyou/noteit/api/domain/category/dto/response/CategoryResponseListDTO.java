package cyou.noteit.api.domain.category.dto.response;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.Getter;

@Getter
public class CategoryResponseListDTO extends BaseEntityDTO {
    private Long categoryId;
    private String categoryName;
}
