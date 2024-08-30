package cyou.noteit.api.domain.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CategoryRequestDTO {
    @NotBlank
    private String categoryName;
    @Positive
    private Long parentCategoryId;
}
