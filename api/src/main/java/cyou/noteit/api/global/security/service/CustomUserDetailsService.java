package cyou.noteit.api.global.security.service;

import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.repository.AccountRepository;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import cyou.noteit.api.global.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetails(
                accountRepository.findByUsername(username)
                        .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND)));
    }
}
