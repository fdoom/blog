package cyou.noteit.api.domain.comment.service;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.comment.dto.request.CommentRequestDTO;
import cyou.noteit.api.domain.comment.dto.request.CommentUpdateRequestDTO;
import cyou.noteit.api.domain.comment.dto.response.CommentResponseDTO;
import cyou.noteit.api.domain.comment.entity.Comment;
import cyou.noteit.api.domain.comment.repository.CommentRepository;
import cyou.noteit.api.domain.post.entity.Post;
import cyou.noteit.api.domain.post.repository.PostRepository;
import cyou.noteit.api.global.config.security.util.SecurityUtil;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final SecurityUtil securityUtil;
    private final PostRepository postRepository;

    @Override
    public ResponseEntity<CommentResponseDTO> createComment(CommentRequestDTO commentRequestDTO) {

        Account account = securityUtil.getAccount();

        Post post = postRepository.findById(commentRequestDTO.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = Comment.builder()
                .account(account)
                .post(post)
                .commentContent(commentRequestDTO.getCommentContent())
                .build();

        if(commentRequestDTO.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(commentRequestDTO.getParentCommentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

            comment.setParentComment(parentComment);
        }

        commentRepository.save(comment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(modelMapper.map(comment, CommentResponseDTO.class));
    }

    @Override
    public ResponseEntity<List<CommentResponseDTO>> getAllCommentInfoList(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return ResponseEntity.ok(
                commentRepository.findAllByPostAndParentCommentIsNull(post)
                        .stream()
                        .map(this::getChildCommentInfoList)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<CommentResponseDTO> updateComment(Long commentId, CommentUpdateRequestDTO commentUpdateRequestDTO) {
        Comment comment = commentRepository.findByCommentIdAndAccount(commentId, securityUtil.getAccount())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        modelMapper.map(commentUpdateRequestDTO, comment);
        commentRepository.save(comment);

        return ResponseEntity.ok(modelMapper.map(comment, CommentResponseDTO.class));
    }

    @Override
    public ResponseEntity<CommentResponseDTO> deleteComment(Long commentId) {
        Comment comment = commentRepository.findByCommentIdAndAccount(commentId, securityUtil.getAccount())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
    }

    private CommentResponseDTO getChildCommentInfoList(Comment comment) {
        CommentResponseDTO commentResponseDTO = modelMapper.map(comment, CommentResponseDTO.class);

        commentResponseDTO.setChildComments(
                comment.getChildComments()
                        .stream()
                        .map(this::getChildCommentInfoList)
                        .toList()
        );
        return commentResponseDTO;
    }
}
