package com.uteexpress.service;

import com.uteexpress.dto.auth.LoginRequest;
import com.uteexpress.dto.auth.RegisterRequest;
import java.util.Map;

public interface AuthService {
    Map<String, String> register(RegisterRequest req);
    Map<String, String> login(LoginRequest req);
}