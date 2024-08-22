package cyou.noteit.api.domain.category.dto.response;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class CategoryResponseDTO extends BaseEntityDTO {
    private Long categoryId;
    private String categoryName;

    @Setter
    private List<CategoryResponseDTO> children;
}
