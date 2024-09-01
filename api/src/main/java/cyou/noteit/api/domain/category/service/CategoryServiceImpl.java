package cyou.noteit.api.domain.category.service;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.category.dto.request.CategoryRequestDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseListDTO;
import cyou.noteit.api.domain.category.entity.Category;
import cyou.noteit.api.domain.category.repository.CategoryRepository;
import cyou.noteit.api.global.config.security.util.SecurityUtil;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final SecurityUtil securityUtil;

    @Override
    public ResponseEntity<CategoryResponseDTO> createCategory(CategoryRequestDTO categoryRequestDTO) {
        if(categoryRepository.existsByCategoryName(categoryRequestDTO.getCategoryName()))
            throw new CustomException(ErrorCode.ALREADY_EXISTED_CATEGORY);

        Category category = modelMapper.map(categoryRequestDTO, Category.class);

        if(categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));

            category.setParentCategory(parentCategory);
        }
        categoryRepository.save(category);
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(category, CategoryResponseDTO.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryResponseDTO);
    }

    @Override
    public ResponseEntity<List<CategoryResponseDTO>> getCategoryInfoList() {
        return ResponseEntity.ok(
                categoryRepository.findByParentCategoryIsNull()
                        .stream()
                        .map(this::mapCategoryToDTO)
                        .toList()
        );
    }

    private CategoryResponseDTO mapCategoryToDTO(Category category) {
        CategoryResponseDTO categoryResponseDTO = modelMapper.map(category, CategoryResponseDTO.class);

        categoryResponseDTO.setChildren(
                category.getChildCategories()
                        .stream()
                        .map(this::mapCategoryToDTO) // 재귀 호출
                        .toList()
        );
        return categoryResponseDTO;
    }

    @Override
    public ResponseEntity<CategoryResponseDTO> updateCategoryInfo(Long categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if(categoryRepository.existsByCategoryNameAndCategoryIdNot(categoryRequestDTO.getCategoryName(), categoryId))
            throw new CustomException(ErrorCode.ALREADY_EXISTED_CATEGORY);

        modelMapper.map(categoryRequestDTO, category);

        if(categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        categoryRepository.save(category);

        return ResponseEntity.ok(
                modelMapper.map(category, CategoryResponseDTO.class)
        );
    }

    @Override
    public ResponseEntity<Void> deleteCategoryInfo(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<CategoryResponseDTO>> searchCategoryName(String categoryName) {
        return ResponseEntity.ok(
                categoryRepository.findByCategoryName(categoryName)
                        .stream()
                        .map(this::mapCategoryToDTO)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<List<CategoryResponseListDTO>> getCategoryAllInfo() {
        return ResponseEntity.ok(
                categoryRepository.findAll()
                        .stream()
                        .map(category -> modelMapper.map(category, CategoryResponseListDTO.class))
                        .toList()
        );
    }
}
