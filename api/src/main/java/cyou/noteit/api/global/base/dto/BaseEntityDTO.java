package cyou.noteit.api.global.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntityDTO {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
