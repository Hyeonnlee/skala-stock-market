package com.example.skala.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class PredictService {

    public byte[] runPrediction(MultipartFile file) throws IOException, InterruptedException {
        // 1. 파일을 서버에 저장
        File inputCsv = new File("input.csv");
        file.transferTo(inputCsv);

        // 2. 파이썬 스크립트 실행
        ProcessBuilder builder = new ProcessBuilder("python3", "predict.py", inputCsv.getAbsolutePath());
        builder.redirectErrorStream(true); // 에러 메시지를 표준 출력으로
        Process process = builder.start();

        // 3. 실행 결과 로그 출력 (디버깅용)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python] " + line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("파이썬 예측 스크립트 실행 실패");
        }

        // 4. 예측 결과 이미지 읽기 (예: predict_output.png)
        File resultImage = new File("predict_output.png");
        if (!resultImage.exists()) {
            throw new FileNotFoundException("예측 결과 이미지가 존재하지 않습니다.");
        }

        return java.nio.file.Files.readAllBytes(resultImage.toPath());
    }
}
