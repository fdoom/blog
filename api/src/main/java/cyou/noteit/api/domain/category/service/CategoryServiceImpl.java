package cyou.noteit.api.domain.category.service;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.category.dto.request.CategoryRequestDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseDTO;
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
        Account account = securityUtil.getAccount();
        if(categoryRepository.existsByAccountAndCategoryName(account, categoryRequestDTO.getCategoryName()))
            throw new CustomException(ErrorCode.ALREADY_EXISTED_CATEGORY);

        Category category = Category.builder()
                .account(account)
                .categoryName(categoryRequestDTO.getCategoryName())
                .build();

        if(categoryRequestDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryRequestDTO.getParentCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

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
                categoryRepository.findByAccountAndParentCategoryIsNull(securityUtil.getAccount())
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
        Account account = securityUtil.getAccount();
        Category category = categoryRepository.findByAccountAndCategoryId(account, categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if(categoryRepository.existsByAccountAndCategoryNameAndCategoryIdNot(account, categoryRequestDTO.getCategoryName(), categoryId))
            throw new CustomException(ErrorCode.ALREADY_EXISTED_CATEGORY);

        Category parentCategory = categoryRepository.findByAccountAndCategoryId(account, categoryRequestDTO.getParentCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));

        modelMapper.map(categoryRequestDTO, category);
        category.setParentCategory(parentCategory);
        categoryRepository.save(category);

        return ResponseEntity.ok(
                modelMapper.map(category, CategoryResponseDTO.class)
        );
    }

    @Override
    public ResponseEntity<Void> deleteCategoryInfo(Long categoryId) {
        Category category = categoryRepository.findByAccountAndCategoryId(securityUtil.getAccount(), categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
        return ResponseEntity.ok().build();
    }
}
