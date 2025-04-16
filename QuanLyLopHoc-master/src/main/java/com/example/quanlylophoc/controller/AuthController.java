package com.example.quanlylophoc.controller;

import com.example.quanlylophoc.DTO.Request.LoginRequest;
import com.example.quanlylophoc.DTO.Request.RegisterRequest;
import com.example.quanlylophoc.DTO.Request.UserProfileRequest;
import com.example.quanlylophoc.DTO.Response.*;
import com.example.quanlylophoc.configuration.JwtTokenProvider;
import com.example.quanlylophoc.entity.UserEntity;
import com.example.quanlylophoc.repository.UserRepository;
import com.example.quanlylophoc.service.AuthService;
import com.example.quanlylophoc.service.EmailService;
import com.example.quanlylophoc.service.MinioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final MinioService minioService;
    private final EmailService emailService;


    public AuthController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, AuthService authService, MinioService minioService, EmailService emailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.authService = authService;
        this.minioService = minioService;
        this.emailService = emailService;
    }

    @GetMapping("/test")
    public String testMail() {
        emailService.sendEmail("thanhdao672002@gmail.com", "Hello!", "Test mail from Spring Boot");
        return "Email sent!";
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(
            @PathVariable int id,
            @ModelAttribute UserProfileRequest request
    ) {
        UserEntity updated = authService.updateUserProfile(id, request.getName(),request.getUsername() ,request.getPassword(), request.getAvatar());
        return ResponseEntity.ok(APIResponse.success(updated));
    }

    @PutMapping("/{id}/profileConvertBase64")
    public ResponseEntity<?> updateProfileBase64(
            @PathVariable int id,
            @ModelAttribute UserProfileRequest request
    ) {
        UserEntity updated = authService.updateUserProfileConvertBase64(
                id,
                request.getName(),
                request.getUsername(),
                request.getPassword(),
                request.getAvatar()
        );

        UserResponse response = minioService.toUserResponse(updated);
        return ResponseEntity.ok(APIResponse.success(response));
    }


    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserInforRegisterResponse>> register(@RequestBody RegisterRequest request) {
        var result = authService.register(request);
        return ResponseEntity.ok(APIResponse.success(result));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        if (token.startsWith("Bearer ")) {
            return ResponseEntity.ok(APIResponse.success(token));
        }
        return ResponseEntity.status(401).body(APIResponse.error(401, "Invalid credentials or token" , null));
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<String>> logout(Pageable pageable) {
        return ResponseEntity.ok(APIResponse.success("Logout successful"));
    }

    @GetMapping("/info")
    public ResponseEntity<APIResponse<UserInfoResponse>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(APIResponse.error(400, "Invalid token",null));
        }

        String token = authHeader.substring(7);
        String username = jwtTokenProvider.extractUsername(token);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .build();

        return ResponseEntity.ok(APIResponse.success(userInfo));
    }

    @GetMapping("/user")
    public ResponseEntity<APIResponse<List<UserInfoResponse>>> getAllUsers() {
        return ResponseEntity.ok(APIResponse.success(authService.getAllUsers()));
    }

    @GetMapping("/users")
    public ResponseEntity<APIResponse<List<UserInfoResponse>>> getAllUsersPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(APIResponse.success(authService.getAllUsersWithPaging(page, size)));
    }

    @GetMapping("/paging")
    public ResponseEntity<APIResponse<Page<UserEntity>>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(APIResponse.success(authService.getAllUsersWithPageable(pageable)));
    }

    @GetMapping("/users/custom")
    public ResponseEntity<APIResponse<PagedUserResponse>> getUsersCustomPaging(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(APIResponse.success(authService.getAllUsersWithCustomPaging(page, size)));
    }

    @GetMapping("/users/search")
    public ResponseEntity<APIResponse<PagedUserResponse>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(APIResponse.success(authService.getAllUsersWithSearchPaging(keyword, page, size)));
    }
}
