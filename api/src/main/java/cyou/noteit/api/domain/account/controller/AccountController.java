package cyou.noteit.api.domain.account.controller;

import cyou.noteit.api.domain.account.dto.request.JoinRequestDTO;
import cyou.noteit.api.domain.account.dto.response.JoinResponseDTO;
import cyou.noteit.api.domain.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/join")
    public ResponseEntity<JoinResponseDTO> join(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        return accountService.joinAccount(joinRequestDTO);
    }

    @GetMapping("/test")
    public String test () {
        return "test";
    }
}
