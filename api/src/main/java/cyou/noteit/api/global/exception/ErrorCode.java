package cyou.noteit.api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    ALREADY_EXISTED_USERNAME(HttpStatus.CONFLICT, "The username already exists"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "The account does not exist");
    private final HttpStatus status;
    private final String message;
}
