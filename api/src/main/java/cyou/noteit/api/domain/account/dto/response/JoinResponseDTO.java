package cyou.noteit.api.domain.account.dto.response;

import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.global.base.dto.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class JoinResponseDTO extends BaseEntityDTO {
    private String username;
    private Role role;

    @Builder
    JoinResponseDTO(String username, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        this.username = username;
        this.role = role;
    }
}
