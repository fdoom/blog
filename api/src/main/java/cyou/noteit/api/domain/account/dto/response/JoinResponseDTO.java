package cyou.noteit.api.domain.account.dto.response;

import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.Getter;

@Getter
public class JoinResponseDTO extends BaseEntityDTO {
    private String username;
    private Role role;
}
