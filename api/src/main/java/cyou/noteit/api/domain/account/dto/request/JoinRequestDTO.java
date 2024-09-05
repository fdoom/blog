package cyou.noteit.api.domain.account.dto.request;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class JoinRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
