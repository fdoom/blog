package cyou.noteit.api.domain.category.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.account.repository.AccountRepository;
import cyou.noteit.api.domain.category.dto.request.CategoryRequestDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseDTO;
import cyou.noteit.api.domain.category.dto.response.CategoryResponseListDTO;
import cyou.noteit.api.domain.category.service.CategoryService;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import org.apache.coyote.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    private static String accessToken;

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
    }

    @AfterAll
    public static void tearDown(@Autowired AccountRepository accountRepository) {
        accountRepository.findByUsername("test")
                .ifPresent(accountRepository::delete);
    }

    @Test
    void createCategoryInfo() throws Exception {
        CategoryRequestDTO categoryRequestDTO = CategoryRequestDTO.builder()
                .categoryName("test 카테고리 이름")
                .parentCategoryId(null)
                .build();

        when(categoryService.createCategory(any(CategoryRequestDTO.class)))
                .thenReturn(ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(CategoryResponseDTO.builder()
                                .categoryId(2L)
                                .categoryName("test 카테고리 이름")
                                .children(null)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                        )
                );

        mockMvc.perform(post("/category/info")
                        .header(AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("category-create-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접속용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("parentCategoryId").type("Number").description("부모 카테고리 식별키"),
                                fieldWithPath("categoryName").description("생성할 카테고리 이름")
                        ),
                        responseFields(
                                fieldWithPath("categoryId").description("생성된 카테고리 식별키"),
                                fieldWithPath("categoryName").description("생성된 카테고리 이름"),
                                fieldWithPath("children").type("Array").description("하위 카테고리 목록"),
                                fieldWithPath("createdAt").description("카테고리 생성일"),
                                fieldWithPath("updatedAt").description("카테고리 수정일")
                        ))
                );
    }

    @Test
    void getCategoryInfoList() throws Exception {

        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        categoryResponseDTOS.add(CategoryResponseDTO.builder()
                .categoryId(1L)
                .categoryName("test")
                .children(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        when(categoryService.getCategoryInfoList())
                .thenReturn(ResponseEntity
                        .ok(categoryResponseDTOS));

        mockMvc.perform(get("/category/info"))
                .andExpect(status().isOk())
                .andDo(document("category-get-info",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].categoryId").description("카테고리 식별키"),
                                fieldWithPath("[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].children").description("하위 카테고리 목록"),
                                fieldWithPath("[].createdAt").description("카테고리 생성일"),
                                fieldWithPath("[].updatedAt").description("카테고리 수정일")
                        ))
                );
    }

    @Test
    void updateCategoryInfo() throws Exception {
        CategoryRequestDTO categoryRequestDTO = CategoryRequestDTO.builder()
                .parentCategoryId(2L)
                .categoryName("test")
                .build();

        CategoryResponseDTO categoryResponseDTO = CategoryResponseDTO.builder()
                .categoryId(1L)
                .categoryName("test")
                .children(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        when(categoryService.updateCategoryInfo(any(Long.class), any(CategoryRequestDTO.class)))
                .thenReturn(ResponseEntity.ok(categoryResponseDTO));

        mockMvc.perform(put("/category/info/1")
                        .header(AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequestDTO)))
                .andExpect(status().isOk())
                .andDo(document("category-update-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접속용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("parentCategoryId").description("부모 카테고리 식별키"),
                                fieldWithPath("categoryName").description("카테고리 이름")
                        ),
                        responseFields(
                                fieldWithPath("categoryId").description("생성된 카테고리 식별키"),
                                fieldWithPath("categoryName").description("생성된 카테고리 이름"),
                                fieldWithPath("children").type("Array").description("하위 카테고리 목록"),
                                fieldWithPath("createdAt").description("카테고리 생성일"),
                                fieldWithPath("updatedAt").description("카테고리 수정일")
                        )
                ));
    }

    @Test
    void deleteCategoryInfo() throws Exception {
        when(categoryService.deleteCategoryInfo(any(Long.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/category/info/1")
                        .header(AUTHORIZATION, accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("category-delete-info",
                        preprocessRequest(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접속용 토큰")
                        )
                ));
    }

    @Test
    void searchCategory() throws Exception {
        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        categoryResponseDTOS.add(CategoryResponseDTO.builder()
                .categoryId(1L)
                .categoryName("test")
                .children(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        when(categoryService.searchCategoryName("t"))
                .thenReturn(ResponseEntity.ok(categoryResponseDTOS));

        mockMvc.perform(get("/category/search/t"))
                .andExpect(status().isOk())
                .andDo(document("category-search-info",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].categoryId").description("카테고리 식별키"),
                                fieldWithPath("[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].children").description("하위 카테고리 목록"),
                                fieldWithPath("[].createdAt").description("카테고리 생성일"),
                                fieldWithPath("[].updatedAt").description("카테고리 수정일")
                        ))
                );
    }

    @Test
    void getCategoryAllInfo() throws Exception {
        List<CategoryResponseListDTO> categoryResponseDTOS = new ArrayList<>();
        categoryResponseDTOS.add(CategoryResponseListDTO.builder()
                .categoryId(1L)
                .categoryName("test")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        when(categoryService.getCategoryAllInfo())
                .thenReturn(ResponseEntity.ok(categoryResponseDTOS));

        mockMvc.perform(get("/category/info/list"))
                .andExpect(status().isOk())
                .andDo(document("category-info-list",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].categoryId").description("카테고리 식별키"),
                                fieldWithPath("[].categoryName").description("카테고리 이름"),
                                fieldWithPath("[].createdAt").description("카테고리 생성일"),
                                fieldWithPath("[].updatedAt").description("카테고리 수정일")
                        ))
                );
    }

    @Test
    void getCategoryPostId() throws Exception {
        CategoryResponseDTO categoryResponseDTO = CategoryResponseDTO.builder()
                .categoryId(1L)
                .categoryName("test")
                .children(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(categoryService.getCategoryPostId(any(Long.class)))
                .thenReturn(ResponseEntity.ok(categoryResponseDTO));

        mockMvc.perform(get("/category/info/post/1"))
                .andExpect(status().isOk())
                .andDo(document("category-postId-info",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("categoryId").description("생성된 카테고리 식별키"),
                                fieldWithPath("categoryName").description("생성된 카테고리 이름"),
                                fieldWithPath("children").type("Array").description("하위 카테고리 목록"),
                                fieldWithPath("createdAt").description("카테고리 생성일"),
                                fieldWithPath("updatedAt").description("카테고리 수정일")
                        )
                ));
    }
}