package cyou.noteit.api.global.config.security.util;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.global.config.security.dto.CustomUserDetails;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityUtil {
    public Account getAccount () {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getAccount();
        } else {
            throw new CustomException(ErrorCode.INVALID_ACCOUNT);
        }
    }

    public String getAuthority() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails.getAuthorities().iterator().next().getAuthority();
        } else {
            throw new CustomException(ErrorCode.INVALID_ACCOUNT);
        }
    }
}
