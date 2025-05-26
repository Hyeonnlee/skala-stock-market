package com.example.skala.controller;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class PredictController {

    private static final String FASTAPI_URL = "http://localhost:8000/predict";  // FastAPI 서버 URL

    @PostMapping(value = "/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> predict(@RequestParam("file") MultipartFile file) {
        try {
            // Multipart 데이터를 FastAPI로 전송
            RestTemplate restTemplate = new RestTemplate();

            // Multipart 요청 생성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 파일을 임시 파일로 저장
            File tempFile = File.createTempFile("upload-", ".csv");
            file.transferTo(tempFile);

            // 파일을 Resource로 감싸기
            org.springframework.core.io.Resource fileResource = new org.springframework.core.io.FileSystemResource(tempFile);

            // 멀티파트 바디 구성
            MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<byte[]> response = restTemplate.postForEntity(FASTAPI_URL, requestEntity, byte[].class);

            // 임시 파일 삭제
            tempFile.delete();

            // 이미지 응답 전달
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(response.getBody());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
