package com.studio.controller;

import com.studio.dto.request.LoginRequest;
import com.studio.dto.response.LoginResponse;
import com.studio.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // =========================================================
    // 1. ĐĂNG NHẬP HỆ THỐNG
    // =========================================================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // =========================================================
    // 2. LẤY THÔNG TIN PHIÊN ĐĂNG NHẬP HIỆN TẠI (GET ME)
    // =========================================================
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    // =========================================================
    // 3. ĐĂNG XUẤT HỆ THỐNG
    // =========================================================
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok(java.util.Map.of("message", "Đăng xuất thành công!"));
    }
}
