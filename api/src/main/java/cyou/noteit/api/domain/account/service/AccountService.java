package cyou.noteit.api.domain.account.service;

import cyou.noteit.api.domain.account.dto.request.AlterRequestDTO;
import cyou.noteit.api.domain.account.dto.request.JoinRequestDTO;
import cyou.noteit.api.domain.account.dto.response.JoinResponseDTO;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    ResponseEntity<JoinResponseDTO> joinAccount(JoinRequestDTO joinRequestDTO);

    ResponseEntity<Void> alterPassword(AlterRequestDTO alterRequestDTO);

    ResponseEntity<Void> deleteInfo();
}
