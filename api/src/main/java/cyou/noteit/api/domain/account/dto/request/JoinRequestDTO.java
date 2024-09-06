package cyou.noteit.api.domain.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
