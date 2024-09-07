package cyou.noteit.api.domain.category.dto.response;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseListDTO extends BaseEntityDTO {
    private Long categoryId;
    private String categoryName;

    @Builder
    CategoryResponseListDTO(Long categoryId, String categoryName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
}
