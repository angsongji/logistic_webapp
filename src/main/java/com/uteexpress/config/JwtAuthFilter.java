package com.uteexpress.config;

import com.uteexpress.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // Lấy JWT token từ cookie
        String token = getJwtFromCookie(request);
        
        // Nếu không có trong cookie, thử lấy từ Authorization header
        if (token == null) {
            token = getJwtFromHeader(request);
        }
        
        // Validate và set authentication
        if (token != null && jwtProvider.validateToken(token)) {
            String username = jwtProvider.getUsername(token);
            
            userRepository.findByUsername(username).ifPresent(user -> {
                var authorities = user.getRoles().stream()
                        .map(roleType -> new SimpleGrantedAuthority(roleType.name()))
                        .collect(Collectors.toList());
                
                var authentication = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        authorities
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        
        chain.doFilter(request, response);
    }

    /**
     * Lấy JWT token từ cookie
     */
    private String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "jwt_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Lấy JWT token từ Authorization header (fallback)
     */
    private String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

