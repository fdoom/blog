package cyou.noteit.api.domain.redirect.service;

import cyou.noteit.api.domain.redirect.dto.response.ImageUploadResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RedirectService {
    ResponseEntity<ImageUploadResponseDTO> imageUpload(MultipartFile file) throws IOException;
}
