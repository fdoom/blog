package cyou.noteit.api.domain.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface ReissueService {
    ResponseEntity<Void> reissue(HttpServletRequest request, HttpServletResponse response);
}
