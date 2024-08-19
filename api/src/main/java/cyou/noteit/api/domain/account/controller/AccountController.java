package cyou.noteit.api.domain.account.controller;

import cyou.noteit.api.domain.account.dto.request.JoinRequestDTO;
import cyou.noteit.api.domain.account.dto.response.JoinResponseDTO;
import cyou.noteit.api.domain.account.service.AccountService;
import cyou.noteit.api.global.config.security.dto.CustomUserDetails;
import cyou.noteit.api.global.config.security.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final SecurityUtil securityUtil;

    @PostMapping("/join")
    public ResponseEntity<JoinResponseDTO> join(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        return accountService.joinAccount(joinRequestDTO);
    }

    @GetMapping("/test")
    public String test () {
        return securityUtil.getAccountId() + " " + securityUtil.getAuthority();
    }
}
