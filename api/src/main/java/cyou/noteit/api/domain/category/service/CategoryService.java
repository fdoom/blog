package cyou.noteit.api.domain.category.service;

import cyou.noteit.api.domain.category.dto.request.CategoryRequestDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseListDTO;
import cyou.noteit.api.domain.category.entity.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CategoryService {
    ResponseEntity<CategoryResponseDTO> createCategory(CategoryRequestDTO categoryRequestDTO);

    ResponseEntity<List<CategoryResponseDTO>> getCategoryInfoList();

    ResponseEntity<CategoryResponseDTO> updateCategoryInfo(Long categoryId, CategoryRequestDTO categoryRequestDTO);

    ResponseEntity<Void> deleteCategoryInfo(Long categoryId);

    ResponseEntity<List<CategoryResponseDTO>> searchCategoryName(String categoryName);

    ResponseEntity<List<CategoryResponseListDTO>> getCategoryAllInfo();
}
