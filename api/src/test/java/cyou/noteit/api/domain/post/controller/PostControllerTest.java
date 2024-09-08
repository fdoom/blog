package cyou.noteit.api.domain.post.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.account.repository.AccountRepository;
import cyou.noteit.api.domain.post.dto.request.PostRequestDTO;
import cyou.noteit.api.domain.post.dto.response.PostResponseDTO;
import cyou.noteit.api.domain.post.entity.status.ShareStatus;
import cyou.noteit.api.domain.post.service.PostService;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
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
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    private static String accessToken;

    private static PostRequestDTO postRequestDTO;
    private static PostResponseDTO postResponseDTO;
    private static List<PostResponseDTO> postResponseDTOS;

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

        postRequestDTO = PostRequestDTO.builder()
                .postTitle("test post title")
                .postContent("test post content")
                .shareStatus(ShareStatus.PUBLIC)
                .categoryId(1L)
                .build();

        postResponseDTO = PostResponseDTO.builder()
                .postId(1L)
                .postTitle("test post title")
                .postContent("test post content")
                .shareStatus(ShareStatus.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postResponseDTOS = new ArrayList<>();
        postResponseDTOS.add(postResponseDTO);
    }

    @AfterAll
    public static void tearDown(@Autowired AccountRepository accountRepository) {
        accountRepository.findByUsername("test")
                .ifPresent(accountRepository::delete);
    }

    @Test
    void createPostInfo() throws Exception {
        when(postService.createPostInfo(any(PostRequestDTO.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(postResponseDTO));

        mockMvc.perform(post("/post/info")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andExpect(status().isCreated())
                .andDo(document("post-create-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("categoryId").description("카테고리 식별키"),
                                fieldWithPath("postTitle").description("게시물 제목"),
                                fieldWithPath("postContent").description("게시물 내용"),
                                fieldWithPath("shareStatus").description("게시물 공유 상태")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("게시물 식별키"),
                                fieldWithPath("postTitle").description("게시물 제목"),
                                fieldWithPath("postContent").description("게시물 내용"),
                                fieldWithPath("shareStatus").description("게시물 공유 상태"),
                                fieldWithPath("createdAt").description("게시물 생성일"),
                                fieldWithPath("updatedAt").description("게시물 수정일")
                        ))
                );
    }

    @Test
    void updatePostInfo() throws Exception {
        when(postService.updatePostInfo(any(Long.class), any(PostRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(postResponseDTO));

        mockMvc.perform(put("/post/info/1")
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postRequestDTO)))
                .andExpect(status().isOk())
                .andDo(document("post-update-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("categoryId").description("카테고리 식별키"),
                                fieldWithPath("postTitle").description("게시물 제목"),
                                fieldWithPath("postContent").description("게시물 내용"),
                                fieldWithPath("shareStatus").description("게시물 공유 상태")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("게시물 식별키"),
                                fieldWithPath("postTitle").description("게시물 제목"),
                                fieldWithPath("postContent").description("게시물 내용"),
                                fieldWithPath("shareStatus").description("게시물 공유 상태"),
                                fieldWithPath("createdAt").description("게시물 생성일"),
                                fieldWithPath("updatedAt").description("게시물 수정일")
                        ))
                );
    }

    @Test
    void getPostInfo() throws Exception {
        when(postService.getPostInfo(any(Long.class))).thenReturn(ResponseEntity.ok(postResponseDTO));

        mockMvc.perform(get("/post/info/1")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("post-get-info",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("게시물 식별키"),
                                fieldWithPath("postTitle").description("게시물 제목"),
                                fieldWithPath("postContent").description("게시물 내용"),
                                fieldWithPath("shareStatus").description("게시물 공유 상태"),
                                fieldWithPath("createdAt").description("게시물 생성일"),
                                fieldWithPath("updatedAt").description("게시물 수정일")
                        ))
                );
    }

    @Test
    void getAllPostInfo() throws Exception {
        when(postService.getAllPostInfo(any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(postResponseDTOS));

        mockMvc.perform(get("/post/info")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("post-get-all-info",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].postId").description("게시물 식별키"),
                                fieldWithPath("[].postTitle").description("게시물 제목"),
                                fieldWithPath("[].postContent").description("게시물 내용"),
                                fieldWithPath("[].shareStatus").description("게시물 공유 상태"),
                                fieldWithPath("[].createdAt").description("게시물 생성일"),
                                fieldWithPath("[].updatedAt").description("게시물 수정일")
                        ))
                );
    }

    @Test
    void getAllPostInfoByCategoryId() throws Exception {
        when(postService.getAllPostInfoByCategoryId(any(Long.class), any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(postResponseDTOS));

        mockMvc.perform(get("/post/info/category/1")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("post-get-categoryId-info",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].postId").description("게시물 식별키"),
                                fieldWithPath("[].postTitle").description("게시물 제목"),
                                fieldWithPath("[].postContent").description("게시물 내용"),
                                fieldWithPath("[].shareStatus").description("게시물 공유 상태"),
                                fieldWithPath("[].createdAt").description("게시물 생성일"),
                                fieldWithPath("[].updatedAt").description("게시물 수정일")
                        ))
                );
    }

    @Test
    void deletePostInfo() throws Exception {
        when(postService.deletePostInfo(any(Long.class)))
                .thenReturn(ResponseEntity.ok(postResponseDTO));

        mockMvc.perform(delete("/post/info/1")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("post-delete",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("게시물 식별키"),
                                fieldWithPath("postTitle").description("게시물 제목"),
                                fieldWithPath("postContent").description("게시물 내용"),
                                fieldWithPath("shareStatus").description("게시물 공유 상태"),
                                fieldWithPath("createdAt").description("게시물 생성일"),
                                fieldWithPath("updatedAt").description("게시물 수정일")
                        ))
                );
    }

    @Test
    void searchPostInfoByPostTitle() throws Exception {
        when(postService.searchPostInfoByPostTitle(any(String.class), any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(postResponseDTOS));

        mockMvc.perform(get("/post/search/t")
                        .header(HttpHeaders.AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("post-search",
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("AUTHORIZATION").description("접속용 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].postId").description("게시물 식별키"),
                                fieldWithPath("[].postTitle").description("게시물 제목"),
                                fieldWithPath("[].postContent").description("게시물 내용"),
                                fieldWithPath("[].shareStatus").description("게시물 공유 상태"),
                                fieldWithPath("[].createdAt").description("게시물 생성일"),
                                fieldWithPath("[].updatedAt").description("게시물 수정일")
                        ))
                );
    }
}