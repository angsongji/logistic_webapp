package com.uteexpress.service.impl;

import com.uteexpress.config.JwtProvider;
import com.uteexpress.dto.auth.LoginRequest;
import com.uteexpress.dto.auth.RegisterRequest;
import com.uteexpress.entity.User;
import com.uteexpress.repository.UserRepository;
import com.uteexpress.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder encoder,
                           JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Map<String, String> register(RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent())
            throw new RuntimeException("Username already exists");

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setFullName(req.getFullName());
        u.setPhone(req.getPhone());
        u.setRoles(Set.of(User.RoleType.ROLE_CUSTOMER));
        
        userRepository.save(u);

        return Map.of("message", "Đăng ký thành công!");
    }

    @Override
    public Map<String, String> login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username"));

        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new RuntimeException("Sai mật khẩu");

        String token = jwtProvider.generateToken(user.getUsername());
        String role = user.getRoles()
                .stream()
                .findFirst()
                .map(Enum::name)
                .orElse("ROLE_CUSTOMER");
        return Map.of("token", token, "role", role);
    }
}