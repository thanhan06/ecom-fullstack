package com.vu.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.request.UserUpdationRequest;
import com.vu.api.DTO.response.UserResponse;

/**
 * Interface UserService định nghĩa các phương thức liên quan đến quản lý người dùng, bao gồm:
 * - getAllUsers: Lấy danh sách tất cả người dùng.
 * - getUserByUserId: Lấy thông tin người dùng dựa trên userId
 * - createUser: Tạo một người dùng mới dựa trên yêu cầu UserCreationRequest và trả về thông tin người dùng đã tạo.
 * - updateUser: Cập nhật thông tin người dùng dựa trên userId và yêu cầu UserUpdationRequest,
 * trả về thông tin người dùng đã cập nhật.
 * - getMyInfo: Lấy thông tin của người dùng hiện tại dựa trên ngữ cảnh bảo mật, trả về thông tin người dùng nếu đã xác thực,
 * hoặc ném ApiException nếu chưa xác thực hoặc người dùng không tồn tại.
 */
@Service
public interface UserService {
    public List<UserResponse> getAllUsers();

    public UserResponse getUserByUserId(String user_id);

    public UserResponse createUser(UserCreationRequest userCreationRequest);

    public UserResponse updateUser(String user_id, UserUpdationRequest userUpdationRequest);

    public UserResponse getMyInfo();
}
