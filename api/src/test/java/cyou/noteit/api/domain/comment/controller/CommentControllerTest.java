package cyou.noteit.api.domain.comment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.account.repository.AccountRepository;
import cyou.noteit.api.domain.comment.dto.request.CommentRequestDTO;
import cyou.noteit.api.domain.comment.dto.request.CommentUpdateRequestDTO;
import cyou.noteit.api.domain.comment.dto.response.CommentResponseDTO;
import cyou.noteit.api.domain.comment.service.CommentService;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import org.apache.coyote.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CommentControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private static String accessToken;

    private static CommentRequestDTO commentRequestDTO;
    private static CommentResponseDTO commentResponseDTO;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setUp(@Autowired JwtUtil jwtUtil, @Autowired AccountRepository accountRepository, @Autowired PasswordEncoder passwordEncoder) {
        accountRepository.findByUsername("test")
                .ifPresent(accountRepository::delete);

        Account account = Account.builder()
                .role(Role.ROLE_ADMIN)
                .username("test")
                .password(passwordEncoder.encode("test_password"))
                .build();

        accountRepository.save(account);

        accessToken = "Bearer " + jwtUtil.createJwt("access", "test", Role.ROLE_ADMIN.name(), 600000L);

        commentRequestDTO = CommentRequestDTO.builder()
                .postId(1L)
                .parentCommentId(1L)
                .commentContent("test comment")
                .build();

        commentResponseDTO = CommentResponseDTO.builder()
                .commentId(2L)
                .username("test")
                .commentContent("test comment")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .childComments(new ArrayList<>())
                .build();
    }

    @AfterAll
    public static void tearDown(@Autowired AccountRepository accountRepository) {
        accountRepository.findByUsername("test")
                .ifPresent(accountRepository::delete);
    }

    @Test
    void createComment() throws Exception {
        when(commentService.createComment(any(CommentRequestDTO.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(commentResponseDTO));

        mockMvc.perform(post("/comment/info")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequestDTO)))
                .andExpect(status().isCreated())
                .andDo(document("/comment-create-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("postId").description("게시물 식별키"),
                                fieldWithPath("parentCommentId").description("부모 댓글 식별키"),
                                fieldWithPath("commentContent").description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").description("댓글 식별키"),
                                fieldWithPath("username").description("댓글 작성자"),
                                fieldWithPath("commentContent").description("댓글 내용"),
                                fieldWithPath("createdAt").description("댓글 생성일"),
                                fieldWithPath("updatedAt").description("댓글 수정일"),
                                fieldWithPath("childComments").description("자식 댓글 목록")
                        )
                ));
    }

    @Test
    void getAllCommentInfoList() throws Exception {
        List<CommentResponseDTO> commentResponseDTOList = new ArrayList<>();
        commentResponseDTOList.add(commentResponseDTO);
        when(commentService.getAllCommentInfoList(any(Long.class)))
                .thenReturn(ResponseEntity.ok(commentResponseDTOList));

        mockMvc.perform(get("/comment/info/post/1"))
                .andExpect(status().isOk())
                .andDo(document("comment-get-info",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].commentId").description("댓글 식별키"),
                                fieldWithPath("[].username").description("댓글 작성자"),
                                fieldWithPath("[].commentContent").description("댓글 내용"),
                                fieldWithPath("[].createdAt").description("댓글 생성일"),
                                fieldWithPath("[].updatedAt").description("댓글 수정일"),
                                fieldWithPath("[].childComments").description("자식 댓글 목록")
                        ))
                );
    }

    @Test
    void updateComment() throws Exception {
        CommentUpdateRequestDTO commentUpdateRequestDTO = CommentUpdateRequestDTO.builder()
                .commentContent("test comment")
                .build();

        when(commentService.updateComment(any(Long.class), any(CommentUpdateRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(commentResponseDTO));

        mockMvc.perform(put("/comment/info/1")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentUpdateRequestDTO)))
                .andExpect(status().isOk())
                .andDo(document("comment-update-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접근용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("commentContent").description("변경할 댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").description("댓글 식별키"),
                                fieldWithPath("username").description("댓글 작성자"),
                                fieldWithPath("commentContent").description("댓글 내용"),
                                fieldWithPath("createdAt").description("댓글 생성일"),
                                fieldWithPath("updatedAt").description("댓글 수정일"),
                                fieldWithPath("childComments").description("자식 댓글 목록")
                        ))
                );
    }

    @Test
    void deleteComment() throws Exception {
        when(commentService.deleteComment(any(Long.class)))
                .thenReturn(ResponseEntity.ok(commentResponseDTO));

        mockMvc.perform(delete("/comment/info/1")
                .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("comment-delete-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접근용 토큰")
                        ),
                        responseFields(
                                fieldWithPath("commentId").description("댓글 식별키"),
                                fieldWithPath("username").description("댓글 작성자"),
                                fieldWithPath("commentContent").description("댓글 내용"),
                                fieldWithPath("createdAt").description("댓글 생성일"),
                                fieldWithPath("updatedAt").description("댓글 수정일"),
                                fieldWithPath("childComments").description("자식 댓글 목록")
                        ))
                );
    }
}