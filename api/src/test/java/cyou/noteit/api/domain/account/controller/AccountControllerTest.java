package cyou.noteit.api.domain.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cyou.noteit.api.domain.account.dto.request.AlterRequestDTO;
import cyou.noteit.api.domain.account.dto.request.JoinRequestDTO;
import cyou.noteit.api.domain.account.dto.response.JoinResponseDTO;
import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.account.repository.AccountRepository;
import cyou.noteit.api.domain.account.service.AccountService;
import cyou.noteit.api.global.config.security.jwt.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
    public void join() throws Exception {
        JoinRequestDTO joinRequestDTO = JoinRequestDTO.builder()
                .username("test_username")
                .password("test_password")
                .build();

        when(accountService.joinAccount(any(JoinRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().body(
                        JoinResponseDTO.builder()
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
    public void alter() throws Exception {
        AlterRequestDTO alterRequestDTO = AlterRequestDTO.builder()
                .password("test_password")
                .newPassword("test_new_password")
                .build();

        when(accountService.alterPassword(any(AlterRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/account/alter")
                        .header(AUTHORIZATION, accessToken)
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
    public void deleteInfo() throws Exception {
        when(accountService.deleteInfo())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/account/info")
                        .header(AUTHORIZATION, accessToken))
                .andExpect(status().isOk())
                .andDo(document("account-delete-info",
                        preprocessRequest(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("접속용 토큰")
                        )));
    }
}
