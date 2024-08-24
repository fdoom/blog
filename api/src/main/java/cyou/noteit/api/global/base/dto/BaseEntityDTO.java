package cyou.noteit.api.global.base.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseEntityDTO {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
