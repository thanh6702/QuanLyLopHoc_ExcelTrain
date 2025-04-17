package com.example.quanlylophoc.service;

import com.example.quanlylophoc.DTO.Response.UserResponse;
import com.example.quanlylophoc.entity.UserEntity;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.Normalizer;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class MinioService {
    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.video-bucket}")
    private String bucketVideo;

    @Value("${minio.url}")
    private String minioUrl;

    private final MinioClient minioClient;

    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(MultipartFile file, String objectName) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    public String uploadVideo(MultipartFile file) throws Exception {

        // Lấy tên gốc của file và chuẩn hóa tên file
        String originalFileName = file.getOriginalFilename();
        String safeFileName = Normalizer.normalize(originalFileName, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        String objectName = "video_" + UUID.randomUUID() + "_" + safeFileName;
        System.out.println("Uploading cleaned video name: " + objectName);

        // Kiểm tra và tạo bucket nếu chưa tồn tại
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketVideo).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketVideo).build());
        }

        try (InputStream inputStream = file.getInputStream()) {
            // Upload video lên Minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketVideo)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        // Trả về URL của video đã upload
        return minioUrl + "/" + bucketVideo + "/" + objectName;
    }



    public String convertImageUrlToBase64(String imageUrl) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            byte[] imageBytes = in.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert image to base64", e);
        }
    }


    public UserResponse toUserResponse(UserEntity user) {
        String avatarBase64 = null;
        if (user.getAvatarUrl() != null) {
            avatarBase64 = convertImageUrlToBase64(user.getAvatarUrl());
        }
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .avatarBase64(avatarBase64)
                .build();
    }

}
