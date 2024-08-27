package cyou.noteit.api.domain.post.service;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.category.entity.Category;
import cyou.noteit.api.domain.category.repository.CategoryRepository;
import cyou.noteit.api.domain.post.dto.request.PostRequestDTO;
import cyou.noteit.api.domain.post.dto.response.PostResponseDTO;
import cyou.noteit.api.domain.post.entity.Post;
import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import cyou.noteit.api.domain.post.repository.PostRepository;
import cyou.noteit.api.global.config.security.util.SecurityUtil;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final SecurityUtil securityUtil;

    @Override
    public ResponseEntity<PostResponseDTO> createPostInfo(PostRequestDTO postRequestDTO) {
        Category category = categoryRepository.findById(postRequestDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Account account = securityUtil.getAccount();

        Post post = modelMapper.map(postRequestDTO, Post.class);
        post.setAccount(account);
        post.setCategory(category);

        postRepository.save(post);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(modelMapper.map(post, PostResponseDTO.class));
    }

    @Override
    public ResponseEntity<PostResponseDTO> updatePostInfo(Long postId, PostRequestDTO postRequestDTO) {
        Post post = postRepository.findByPostIdAndAccount(postId, securityUtil.getAccount())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Category category = categoryRepository.findById(postRequestDTO.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        modelMapper.map(postRequestDTO, post);
        post.setCategory(category);

        postRepository.save(post);
        return ResponseEntity
                .ok(modelMapper.map(post, PostResponseDTO.class));
    }

    @Override
    public ResponseEntity<PostResponseDTO> getPostInfo(Long postId) {
        return ResponseEntity.ok(
                modelMapper.map(
                        postRepository.findByPostIdAndShareStatus(postId, roleToShareStatus())
                                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND))
                        , PostResponseDTO.class)
        );
    }

    @Override
    public ResponseEntity<List<PostResponseDTO>> getAllPostInfo(Pageable pageable) {
        return ResponseEntity.ok(
                postRepository.findAllByShareStatus(roleToShareStatus(), pageable)
                        .stream()
                        .map(post -> modelMapper.map(post, PostResponseDTO.class))
                        .toList()
        );
    }

    @Override
    public ResponseEntity<List<PostResponseDTO>> getAllPostInfoByCategoryId(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        return ResponseEntity.ok(
                postRepository.findAllByShareStatusAndCategory(roleToShareStatus(), category.getCategoryId(), pageable)
                        .stream()
                        .map(post -> modelMapper.map(post, PostResponseDTO.class))
                        .toList()
        );
    }

    @Override
    public ResponseEntity<PostResponseDTO> deletePostInfo(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<PostResponseDTO>> searchPostInfoByPostTitle(String postTitle, Pageable pageable) {
        return ResponseEntity.ok(
                postRepository.findAllByPostTitle(postTitle, roleToShareStatus(), pageable)
                        .stream()
                        .map(post -> modelMapper.map(post, PostResponseDTO.class))
                        .toList()
        );
    }

    private String roleToShareStatus() {
        return switch(securityUtil.getRole()) {
            case ROLE_VISITOR -> ShareStatus.PUBLIC.name();
            case ROLE_USER -> ShareStatus.PROTECTED.name();
            case ROLE_ADMIN -> ShareStatus.PRIVATE.name();
        };
    }
}
