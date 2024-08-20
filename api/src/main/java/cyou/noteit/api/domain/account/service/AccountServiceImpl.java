package cyou.noteit.api.domain.account.service;

import cyou.noteit.api.domain.account.dto.request.AlterRequestDTO;
import cyou.noteit.api.domain.account.dto.request.JoinRequestDTO;
import cyou.noteit.api.domain.account.dto.response.JoinResponseDTO;
import cyou.noteit.api.domain.account.entity.Account;
import cyou.noteit.api.domain.account.entity.role.Role;
import cyou.noteit.api.domain.account.repository.AccountRepository;
import cyou.noteit.api.global.config.security.util.SecurityUtil;
import cyou.noteit.api.global.exception.CustomException;
import cyou.noteit.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final SecurityUtil securityUtil;

    @Override
    public ResponseEntity<JoinResponseDTO> joinAccount(JoinRequestDTO joinRequestDTO) {
        Role role = accountRepository.countAllAccount() == 0 ? Role.ROLE_ADMIN : Role.ROLE_USER;

        if(accountRepository.existsByUsername(joinRequestDTO.getUsername()))
            throw new CustomException(ErrorCode.ALREADY_EXISTED_USERNAME);

        Account savedEntity = accountRepository.save(
                Account.builder()
                        .username(joinRequestDTO.getUsername())
                        .password(passwordEncoder.encode(joinRequestDTO.getPassword()))
                        .role(role)
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(modelMapper
                        .map(savedEntity, JoinResponseDTO.class)
                );
    }

    @Override
    public ResponseEntity<Void> alterPassword(AlterRequestDTO alterRequestDTO) {
        Account account = securityUtil.getAccount();
        if(!passwordEncoder.matches(alterRequestDTO.getPassword(), account.getPassword()))
            throw new CustomException(ErrorCode.INVALID_ACCOUNT_PASSWORD);

        account.alterPassword(passwordEncoder.encode(alterRequestDTO.getNewPassword()));
        accountRepository.save(account);
        return ResponseEntity.ok().build();
    }
}
