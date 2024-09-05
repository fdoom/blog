package cyou.noteit.api.domain.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AlterRequestDTO {
    @NotBlank
    private String password;

    @NotBlank
    private String newPassword;
}
