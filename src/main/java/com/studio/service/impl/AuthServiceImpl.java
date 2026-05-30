package com.studio.service.impl;

import com.studio.dto.request.LoginRequest;
import com.studio.dto.response.LoginResponse;
import com.studio.entity.User;
import com.studio.repository.UserRepository;
import com.studio.security.JwtTokenProvider;
import com.studio.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 1. Thực hiện xác thực thông qua AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Truy vấn thông tin người dùng từ Database
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản đăng nhập."));

        String roleName = user.getRole().getRoleName();
        String avatarUrl = user.getStaffProfile() != null ? user.getStaffProfile().getAvatarUrl() : null;

        // 3. Sinh token JWT
        String jwtToken = tokenProvider.generateToken(user.getUsername(), roleName);

        return LoginResponse.builder()
                .token(jwtToken)
                .tokenType("Bearer")
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(roleName)
                .avatarUrl(avatarUrl)
                .build();
    }

    @Override
    public Map<String, Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Chưa xác thực hoặc phiên đăng nhập đã hết hạn.");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản đăng nhập."));

        String roleName = user.getRole().getRoleName();
        String avatarUrl = user.getStaffProfile() != null ? user.getStaffProfile().getAvatarUrl() : null;

        return Map.of(
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "role", roleName,
                "avatarUrl", avatarUrl != null ? avatarUrl : ""
        );
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
