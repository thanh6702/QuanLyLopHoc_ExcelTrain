package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.LoginRequest;
import com.example.quanlylophoc.DTO.Request.RegisterRequest;
import com.example.quanlylophoc.DTO.Response.PagedUserResponse;
import com.example.quanlylophoc.DTO.Response.UserInfoResponse;
import com.example.quanlylophoc.DTO.Response.UserInforRegisterResponse;
import com.example.quanlylophoc.configuration.JwtTokenProvider;
import com.example.quanlylophoc.entity.UserEntity;
import com.example.quanlylophoc.repository.UserRepository;
import com.example.quanlylophoc.service.AuthService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<UserInforRegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfoResponse>> getAllUsersPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(authService.getAllUsersWithPaging(page, size));
    }
    @GetMapping("/paging")
    public ResponseEntity<Page<UserEntity>> getAllUsers(Pageable pageable) {
        Page<UserEntity> users = authService.getAllUsersWithPageable(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/custom")
    public ResponseEntity<PagedUserResponse> getUsersCustomPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(authService.getAllUsersWithCustomPaging(page, size));
    }

    @GetMapping("/users/search")
    public ResponseEntity<PagedUserResponse> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(authService.getAllUsersWithSearchPaging(keyword, page, size));
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

    @GetMapping("/user")
    public ResponseEntity<List<UserInfoResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
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
    public ResponseEntity<String> logout(Pageable pageable) {
        return ResponseEntity.ok("Logout successful");
    }
}
