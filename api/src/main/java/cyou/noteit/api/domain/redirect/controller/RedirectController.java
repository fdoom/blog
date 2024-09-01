package cyou.noteit.api.domain.redirect.controller;

import cyou.noteit.api.domain.redirect.dto.response.ImageUploadResponseDTO;
import cyou.noteit.api.domain.redirect.service.RedirectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class RedirectController {
    private final RedirectService redirectService;

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponseDTO> imageUpload(
            @RequestPart MultipartFile file
    ) throws IOException {
        return redirectService.imageUpload(file);
    }
}
