package cyou.noteit.api.domain.comment.controller;

import cyou.noteit.api.domain.comment.dto.request.CommentRequestDTO;
import cyou.noteit.api.domain.comment.dto.request.CommentUpdateRequestDTO;
import cyou.noteit.api.domain.comment.dto.response.CommentResponseDTO;
import cyou.noteit.api.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/info")
    public ResponseEntity<CommentResponseDTO> createComment(@Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        return commentService.createComment(commentRequestDTO);
    }

    @GetMapping("/info/post/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getAllCommentInfoList(@PathVariable Long postId) {
        return commentService.getAllCommentInfoList(postId);
    }

    @PutMapping("/info/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentUpdateRequestDTO commentUpdateRequestDTO) {
        return commentService.updateComment(commentId, commentUpdateRequestDTO);
    }

    @DeleteMapping("/info/{commentId}")
    public ResponseEntity<CommentResponseDTO> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
