package com.vu.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lớp ApiApplication là điểm khởi đầu của ứng dụng Spring Boot. Nó được đánh dấu với @SpringBootApplication,
 * cho phép Spring Boot tự động cấu hình ứng dụng dựa trên các thành phần đã được khai báo trong classpath.
 * Phương thức main chạy ứng dụng bằng cách gọi SpringApplication.run, truyền vào ApiApplication.class và các đối số dòng lệnh.
 */
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
