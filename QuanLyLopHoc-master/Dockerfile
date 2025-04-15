# Sử dụng Java 17 JDK
FROM eclipse-temurin:17-jdk-alpine

# Tạo thư mục app trong container
WORKDIR /app

# Copy file jar từ thư mục target sau khi build
COPY target/quanlylophoc-0.0.1-SNAPSHOT.jar app.jar


# Expose cổng mặc định của Spring Boot
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
