package cyou.noteit.api.domain.category.controller;

import cyou.noteit.api.domain.category.dto.request.CategoryRequestDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseDTO;
import cyou.noteit.api.domain.category.entity.Category;
import cyou.noteit.api.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/info")
    public ResponseEntity<CategoryResponseDTO> createCategoryInfo(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return categoryService.createCategory(categoryRequestDTO);
    }

    @GetMapping("/info")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoryInfoList() {
        return categoryService.getCategoryInfoList();
    }

    @PutMapping("/info/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategoryInfo(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return categoryService.updateCategoryInfo(categoryId, categoryRequestDTO);
    }

    @DeleteMapping("/info/{categoryId}")
    public ResponseEntity<Void> deleteCategoryInfo(@PathVariable Long categoryId) {
        return categoryService.deleteCategoryInfo(categoryId);
    }

    @GetMapping("/search/{categoryName}")
    public ResponseEntity<List<CategoryResponseDTO>> searchCategory(@PathVariable String categoryName) {
        return categoryService.searchCategoryName(categoryName);
    }
}
