package cyou.noteit.api.domain.post.service;

import cyou.noteit.api.domain.post.dto.request.PostRequestDTO;
import cyou.noteit.api.domain.post.dto.response.PostResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PostService {
    ResponseEntity<PostResponseDTO> createPostInfo(PostRequestDTO postRequestDTO);

    ResponseEntity<PostResponseDTO> updatePostInfo(Long postId, PostRequestDTO postRequestDTO);

    ResponseEntity<PostResponseDTO> getPostInfo(Long postId);

    ResponseEntity<List<PostResponseDTO>> getAllPostInfo();

    ResponseEntity<List<PostResponseDTO>> getAllPostInfoByCategoryId(Long categoryId);

    ResponseEntity<PostResponseDTO> deletePostInfo(Long postId);

    ResponseEntity<List<PostResponseDTO>> searchPostInfoByPostTitle(String postTitle);
}
