    package com.example.quanlylophoc.service;

    import com.example.quanlylophoc.DTO.Request.LoginRequest;
    import com.example.quanlylophoc.DTO.Request.RegisterRequest;
    import com.example.quanlylophoc.configuration.JwtTokenProvider;
    import com.example.quanlylophoc.entity.UserEntity;
    import com.example.quanlylophoc.repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;
    import java.util.Optional;


    @Service
    public class AuthService {
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private JwtTokenProvider jwtTokenProvider;


        public String register(RegisterRequest request) {
            Optional<UserEntity> existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser.isPresent()) {
                return "Username already taken";
            }

            UserEntity newUser = new UserEntity();
            newUser.setName(request.getName());
            newUser.setUsername(request.getUsername());
            newUser.setPassword(request.getPassword()); // Gợi ý: dùng BCrypt

            userRepository.save(newUser);

            return "User registered successfully";
        }

        public String login(LoginRequest request) {
            Optional<UserEntity> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                if (user.getPassword().equals(request.getPassword())) {
                    String token = jwtTokenProvider.generateToken(user.getUsername());
                    System.out.println("Generated token: " + token);
                    return "Bearer " + token;  // Trả về token với prefix "Bearer "
                } else {
                    return "Invalid password";
                }
            }
            return "User not found";  // Nếu không tìm thấy user
        }


    }
