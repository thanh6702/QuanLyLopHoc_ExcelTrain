package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.LoginRequest;
import com.example.quanlylophoc.DTO.Request.RegisterRequest;
import com.example.quanlylophoc.DTO.Response.UserInfoResponse;
import com.example.quanlylophoc.configuration.JwtTokenProvider;
import com.example.quanlylophoc.entity.UserEntity;
import com.example.quanlylophoc.repository.UserRepository;
import com.example.quanlylophoc.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository,AuthService authService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        System.out.println("Received login request: " + request);  // Debug log

        String token = authService.login(request);

        if (token.startsWith("Bearer ")) {
            return ResponseEntity.ok(token);
        } else {
            System.out.println("Login failed, invalid token: " + token);
            return ResponseEntity.status(401).body("Invalid credentials or token");
        }
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        String token = authHeader.substring(7);
        String username = jwtTokenProvider.extractUsername(token); // lấy username từ token

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .build();

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}
