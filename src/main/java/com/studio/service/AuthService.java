package com.studio.service;

import com.studio.dto.request.LoginRequest;
import com.studio.dto.response.LoginResponse;
import java.util.Map;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    Map<String, Object> getCurrentUser();
    void logout();
}
