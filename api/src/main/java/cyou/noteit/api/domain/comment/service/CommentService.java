package cyou.noteit.api.domain.comment.service;

import cyou.noteit.api.domain.comment.dto.request.CommentRequestDTO;
import cyou.noteit.api.domain.comment.dto.request.CommentUpdateRequestDTO;
import cyou.noteit.api.domain.comment.dto.response.CommentResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    ResponseEntity<CommentResponseDTO> createComment(CommentRequestDTO commentRequestDTO);

    ResponseEntity<List<CommentResponseDTO>> getAllCommentInfoList(Long postId);

    ResponseEntity<CommentResponseDTO> updateComment(Long commentId, CommentUpdateRequestDTO commentUpdateRequestDTO);

    ResponseEntity<CommentResponseDTO> deleteComment(Long commentId);
}
