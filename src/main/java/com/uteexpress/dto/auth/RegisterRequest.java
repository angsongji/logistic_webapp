package com.uteexpress.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String fullName;
    private String phone;
}