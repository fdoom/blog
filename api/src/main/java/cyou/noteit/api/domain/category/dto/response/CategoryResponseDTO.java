package cyou.noteit.api.domain.category.dto.response;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO extends BaseEntityDTO {
    private Long categoryId;
    private String categoryName;

    @Setter
    private List<CategoryResponseDTO> children;

    @Builder
    CategoryResponseDTO(Long categoryId, String categoryName, List<CategoryResponseDTO> children, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.children = children;
    }
}
