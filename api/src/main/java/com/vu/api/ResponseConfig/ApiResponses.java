package com.vu.api.ResponseConfig;

import java.net.URI;
import java.time.Instant;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Class ApiResponses để cung cấp các phương thức tiện ích để xây dựng các phản hồi API chuẩn,
 * bao gồm các phương thức như ok (trả về 200 OK), created (trả về 201 Created) và build (xây dựng phản hồi tùy chỉnh).
 * Các phương thức này giúp chuẩn hóa cấu trúc phản hồi của API, bao gồm timestamp, status, message, path và data.
 */
public class ApiResponses {
    // Private constructor để ngăn chặn việc khởi tạo đối tượng của lớp này, vì tất cả các phương thức đều là static.
    private ApiResponses() {}

    /**
     * Phương thức ok để trả về phản hồi thành công với mã trạng thái 200 OK, bao gồm dữ liệu trả về (data).
     * @param req HttpServletRequest để lấy thông tin về đường dẫn yêu cầu (path).
     * @param data Dữ liệu trả về trong phản hồi.
     * @return ResponseEntity chứa ApiResponse với thông tin phản hồi chuẩn.
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(HttpServletRequest req, T data) {
        return build(req, HttpStatus.OK, "OK", data);
    }

    /**
     * Phương thức created để trả về phản hồi thành công với mã trạng thái 201 Created, bao gồm dữ liệu trả về (data).
     * @param <T> Kiểu dữ liệu của phản hồi.
     * @param req HttpServletRequest để lấy thông tin về đường dẫn yêu cầu (path).
     * @param data Dữ liệu trả về trong phản hồi.
     * @return ResponseEntity chứa ApiResponse với thông tin phản hồi chuẩn.
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(HttpServletRequest req, T data) {
        return build(req, HttpStatus.CREATED, "Created", data);
    }

    /**
     * Phương thức created để trả về phản hồi thành công với mã trạng thái 201 Created,
     * bao gồm dữ liệu trả về (data) và header Location chỉ định URI của tài nguyên mới được tạo.
     * @param <T> Kiểu dữ liệu của phản hồi.
     * @param req HttpServletRequest để lấy thông tin về đường dẫn yêu cầu (path).
     * @param location URI của tài nguyên mới được tạo, sẽ được đặt trong header Location của phản hồi.
     * @param data Dữ liệu trả về trong phản hồi.
     * @return ResponseEntity chứa ApiResponse với thông tin phản hồi chuẩn và header Location.
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(HttpServletRequest req, URI location, T data) {
        ApiResponse<T> body =
                new ApiResponse<>(Instant.now(), HttpStatus.CREATED.value(), "Created", req.getRequestURI(), data);
        return ResponseEntity.created(location).body(body);
    }

    /**
     * Phương thức build để xây dựng phản hồi tùy chỉnh với mã trạng thái,
     * thông điệp và dữ liệu trả về do người dùng chỉ định.
     * @param <T> Kiểu dữ liệu của phản hồi.
     * @param req HttpServletRequest để lấy thông tin về đường dẫn yêu cầu (path).
     * @param status Mã trạng thái HTTP của phản hồi.
     * @param message Thông điệp phản hồi.
     * @param data Dữ liệu trả về trong phản hồi.
     * @return ResponseEntity chứa ApiResponse với thông tin phản hồi chuẩn theo các tham số đã chỉ định.
     */
    public static <T> ResponseEntity<ApiResponse<T>> build(
            HttpServletRequest req, HttpStatus status, String message, T data) {
        ApiResponse<T> body = new ApiResponse<>(Instant.now(), status.value(), message, req.getRequestURI(), data);
        return ResponseEntity.status(status).body(body);
    }
}
