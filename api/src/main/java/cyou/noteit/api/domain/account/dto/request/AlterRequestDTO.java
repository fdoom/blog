package cyou.noteit.api.domain.account.dto.request;

import lombok.Getter;

@Getter
public class AlterRequestDTO {
    private String password;
    private String newPassword;
}
