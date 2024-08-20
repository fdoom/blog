package cyou.noteit.api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    ALREADY_EXISTED_USERNAME(HttpStatus.CONFLICT, "The username already exists"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "The account does not exist"),

    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "The refresh token does not exist"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "The refresh token is expired"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "The refresh token is invalid"),
    LOGIN_REQUEST_BODY_NOT_FOUND(HttpStatus.BAD_REQUEST, "The username and password do not exist"),

    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "The access token is expired"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "The access token is invalid"),
    INVALID_ACCOUNT(HttpStatus.UNAUTHORIZED, "The account is invalid"),
    INVALID_ACCOUNT_PASSWORD(HttpStatus.UNAUTHORIZED, "The account password is invalid"),;
    private final HttpStatus status;
    private final String message;
}
