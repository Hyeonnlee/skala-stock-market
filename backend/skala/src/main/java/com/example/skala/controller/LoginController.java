package com.example.skala.controller;

import com.example.skala.dto.LoginRequest;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        // 간단한 로그인 검증 로직 (예시)
        if ("admin".equals(request.getUserId()) && "1234".equals(request.getPassword()) && request.isAdmin()) {
            response.put("message", "관리자 로그인 성공");
        } else if ("user".equals(request.getUserId()) && "1234".equals(request.getPassword()) && !request.isAdmin()) {
            response.put("message", "일반 사용자 로그인 성공");
        } else {
            response.put("message", "로그인 실패: 정보가 일치하지 않습니다.");
        }

        return response;
    }
}