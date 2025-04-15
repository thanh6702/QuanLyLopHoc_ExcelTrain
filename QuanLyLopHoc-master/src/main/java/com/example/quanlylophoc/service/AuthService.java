    package com.example.quanlylophoc.service;

    import com.example.quanlylophoc.DTO.Request.LoginRequest;
    import com.example.quanlylophoc.DTO.Request.RegisterRequest;
    import com.example.quanlylophoc.DTO.Response.PagedUserResponse;
    import com.example.quanlylophoc.DTO.Response.UserInfoResponse;
    import com.example.quanlylophoc.DTO.Response.UserInforRegisterResponse;
    import com.example.quanlylophoc.Exception.AppException;
    import com.example.quanlylophoc.Exception.BadRequestException;
    import com.example.quanlylophoc.Exception.ErrorCode;
    import com.example.quanlylophoc.configuration.JwtTokenProvider;
    import com.example.quanlylophoc.entity.UserEntity;
    import com.example.quanlylophoc.repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.server.ResponseStatusException;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;


    @Service
    public class AuthService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider jwtTokenProvider;
        private final MinioService minioService;

        public AuthService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider, MinioService minioService) {
            this.userRepository = userRepository;
            this.passwordEncoder = passwordEncoder;
            this.jwtTokenProvider = jwtTokenProvider;
            this.minioService = minioService;
        }


        public UserEntity updateUserProfile(int userId, String name,String username ,String password,MultipartFile avatar) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (name != null) {
                user.setName(name);
            }
            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            if (username != null && !username.isBlank()) {
                user.setUsername(username);
            }
            if (avatar != null && !avatar.isEmpty()) {
                String objectName = "avatars/user_" + userId + "_" + System.currentTimeMillis();
                String avatarUrl = minioService.uploadFile(avatar, objectName);
                user.setAvatarUrl(avatarUrl);
            }
            return userRepository.save(user);
        }

        public UserEntity updateUserProfileConvertBase64(
                int userId,
                String name,
                String username,
                String password,
                MultipartFile avatar
        ) {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (name != null) {
                user.setName(name);
            }
            if (username != null && !username.isBlank()) {
                user.setUsername(username);
            }
            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            if (avatar != null && !avatar.isEmpty()) {
                String objectName = "avatars/user_" + userId + "_" + System.currentTimeMillis();
                String avatarUrl = minioService.uploadFile(avatar, objectName);
                user.setAvatarUrl(avatarUrl); // Chỉ lưu URL, base64 chỉ dùng để response
            }

            return userRepository.save(user);
        }

        public UserInforRegisterResponse register(RegisterRequest request) {
            Optional<UserEntity> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) {
                throw new BadRequestException(ErrorCode.USERNAME_ALREADY_EXISTS);
            }

            String encodedPassword = passwordEncoder.encode(request.getPassword());

            UserEntity newUser = new UserEntity();
            newUser.setName(request.getName());
            newUser.setUsername(request.getUsername());
            newUser.setPassword(encodedPassword);

            UserEntity savedUser = userRepository.save(newUser);

            String token = jwtTokenProvider.generateToken(savedUser.getUsername());


            return UserInforRegisterResponse.builder()
                    .id(savedUser.getId())
                    .name(savedUser.getName())
                    .username(savedUser.getUsername())
                    .token(token)
                    .build();
        }

        public List<UserInfoResponse> getAllUsers() {
            return userRepository.findAll().stream()
                    .map(user -> UserInfoResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .username(user.getUsername())
                            .build())
                    .collect(Collectors.toList());
        }

        public PagedUserResponse getAllUsersWithCustomPaging(int page, int size) {
            int offset = page * size;
            List<UserEntity> userEntities = userRepository.findAllUsersWithPagination(size, offset);
            int total = userRepository.countTotalUsers();

            // Convert sang DTO
            List<UserInfoResponse> users = userEntities.stream()
                    .map(user -> UserInfoResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .username(user.getUsername())
                            .build())
                    .toList();

            return PagedUserResponse.builder()
                    .users(users)
                    .total(total)
                    .build();
        }


        public PagedUserResponse getAllUsersWithSearchPaging(String keyword, int page, int size) {
            int offset = page * size;

            List<UserEntity> userEntities = userRepository
                    .searchUsersWithPagination(keyword, size, offset);

            int total = userRepository.countSearchUsers(keyword);

            List<UserInfoResponse> users = userEntities.stream()
                    .map(user -> UserInfoResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .username(user.getUsername())
                            .build())
                    .toList();

            return PagedUserResponse.builder()
                    .users(users)
                    .total(total)
                    .build();
        }



        public Page<UserEntity> getAllUsersWithPageable(Pageable pageable) {
            return userRepository.findAllUsersWithPageable(pageable);
        }


        public List<UserInfoResponse> getAllUsersWithPaging(int page, int size) {
            int offset = page * size;
            List<UserEntity> users = userRepository.findAllUsersWithPagination(size, offset);

            return users.stream()
                    .map(user -> UserInfoResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .username(user.getUsername())
                            .build())
                    .collect(Collectors.toList());
        }


        public String login(LoginRequest request) {
            Optional<UserEntity> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                throw new BadRequestException(ErrorCode.USER_NOT_FOUND);
            }

            UserEntity user = userOpt.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
            }

            String token = jwtTokenProvider.generateToken(user.getUsername());
            return "Bearer " + token;
        }



    }
