package cyou.noteit.api.domain.post.controller;

import cyou.noteit.api.domain.post.dto.request.PostRequestDTO;
import cyou.noteit.api.domain.post.dto.response.PostResponseDTO;
import cyou.noteit.api.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/info")
    public ResponseEntity<PostResponseDTO> createPostInfo(@RequestBody PostRequestDTO postRequestDTO) {
        return postService.createPostInfo(postRequestDTO);
    }

    @PutMapping("/info/{postId}")
    public ResponseEntity<PostResponseDTO> updatePostInfo(@PathVariable Long postId, @RequestBody PostRequestDTO postRequestDTO) {
        return postService.updatePostInfo(postId, postRequestDTO);
    }

    @GetMapping("/info/{postId}")
    public ResponseEntity<PostResponseDTO> getPostInfo(@PathVariable Long postId) {
        return postService.getPostInfo(postId);
    }

    @GetMapping("/info")
    public ResponseEntity<List<PostResponseDTO>> getAllPostInfo() {
        return postService.getAllPostInfo();
    }

    @GetMapping("/info/category/{categoryId}")
    public ResponseEntity<List<PostResponseDTO>> getAllPostInfoByCategoryId(@PathVariable Long categoryId) {
        return postService.getAllPostInfoByCategoryId(categoryId);
    }

    @DeleteMapping("/info/{postId}")
    public ResponseEntity<PostResponseDTO> deletePostInfo(@PathVariable Long postId) {
        return postService.deletePostInfo(postId);
    }
}
