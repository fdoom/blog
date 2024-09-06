package cyou.noteit.api.domain.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.domain.account.dto.request.AlterRequestDTO;
import cyou.noteit.api.domain.account.dto.request.JoinRequestDTO;
import cyou.noteit.api.domain.account.dto.response.JoinResponseDTO;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.account.service.AccountService;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import io.jsonwebtoken.JwtBuilder;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockCookie;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private String accessToken;

    @BeforeEach
    public void setUp() {
        accessToken = jwtUtil.createJwt("access", "test", Role.ROLE_ADMIN.name(), 600000L);
    }

    @Test
    public void join() throws Exception {
        JoinRequestDTO joinRequestDTO = JoinRequestDTO.builder()
                .username("test_username")
                .password("test_password")
                .build();

        when(accountService
                .joinAccount(any(JoinRequestDTO.class)))
                .thenReturn(ResponseEntity
                        .ok()
                        .body(JoinResponseDTO.builder()
                                .role(Role.ROLE_ADMIN)
                                .username("test_username")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build())
                );

        mockMvc.perform(post("/account/join")
                        .content(objectMapper.writeValueAsString(joinRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("account-join",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").description("사용자 이름"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("username").description("사용자 이름"),
                                fieldWithPath("role").description("사용자 역할"),
                                fieldWithPath("createdAt").description("생성 시간"),
                                fieldWithPath("updatedAt").description("수정 시간")
                        )
                ));
    }

    @Test
    @WithMockUser(username = "test", authorities = {"ROLE_ADMIN"})
    public void alter() throws Exception {
        AlterRequestDTO alterRequestDTO = AlterRequestDTO.builder()
                .password("test_password")
                .newPassword("test_new_password")
                .build();

        when(accountService.alterPassword(any(AlterRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/account/alter")
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alterRequestDTO)))
                .andExpect(status().isOk())
                .andDo(document("account-alter",
                        preprocessRequest(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접속용 토큰")
                        ),
                        requestFields(
                                fieldWithPath("password").description("현재 사용자의 비밀번호"),
                                fieldWithPath("newPassword").description("변경할 사용자의 비밀번호")
                        )
                ));
    }

    @Test
    @WithMockUser(username = "test", authorities = {"ROLE_ADMIN"})
    public void deleteInfo() throws Exception {
        when(accountService.deleteInfo())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/account/info")
                        .header(AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andDo(document("account-delete-info",
                        preprocessRequest(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접속용 토큰")
                        )));
    }
}
