package cyou.noteit.api.domain.redirect.service;

import cyou.noteit.api.domain.redirect.dto.request.ImageUploadRequestDTO;
import cyou.noteit.api.domain.redirect.dto.response.ImageUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RedirectServiceImpl implements RedirectService {
    private final RestTemplate restTemplate;

    @Value("${IMAGE_API_URL}")
    private String IMAGE_API_URL;

    @Value("${IMAGE_API_SECRET}")
    private String IMAGE_API_SECRET;

    @Override
    public ResponseEntity<ImageUploadResponseDTO> imageUpload(MultipartFile file) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ImageUploadRequestDTO imageUploadRequestDTO = new ImageUploadRequestDTO(IMAGE_API_SECRET);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("imageUploadRequestDTO", imageUploadRequestDTO);
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ImageUploadResponseDTO> responseEntity = restTemplate.postForEntity(
                IMAGE_API_URL,
                requestEntity,
                ImageUploadResponseDTO.class
        );

        return ResponseEntity.status(responseEntity.getStatusCode())
                .body(responseEntity.getBody());
    }
}
