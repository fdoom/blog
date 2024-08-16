package cyou.noteit.api.domain.account.dto.request;

import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.Getter;

@Getter
public class JoinRequestDTO {
    private String username;
    private String password;
}
