package com.project.parkrental.security.Controller;

import com.project.parkrental.security.JwtUtil;
import com.project.parkrental.security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String username = jwtUtil.extractUsername(refreshToken);

        if (jwtUtil.validateRefreshToken(refreshToken)) {
            String newAccessToken = jwtUtil.generateToken(username);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효한 토큰이 아닙니다.");
        }
    }
}
