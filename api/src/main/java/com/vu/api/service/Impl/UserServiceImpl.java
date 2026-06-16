package com.vu.api.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vu.api.DTO.request.UserCreationRequest;
import com.vu.api.DTO.request.UserUpdationRequest;
import com.vu.api.DTO.response.UserResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.UserEntity;
import com.vu.api.mapper.UserMapper;
import com.vu.api.repository.UserRepository;
import com.vu.api.service.UserService;

import lombok.experimental.FieldDefaults;

/**
 * Class UserServiceImpl triển khai các phương thức của UserService để quản lý người dùng.
 * Nó sử dụng UserRepository để lưu trữ và truy xuất thông tin người dùng từ cơ sở dữ liệu,
 * UserMapper để chuyển đổi giữa UserEntity và UserResponse, và PasswordEncoder để mã hóa mật khẩu người dùng.
 * Các phương thức bao gồm getAllUsers để lấy danh sách tất cả người dùng, getUserByUserId để lấy thông tin người dùng theo userId,
 * createUser để tạo người dùng mới, updateUser để cập nhật thông tin người dùng,
 * và getMyInfo để lấy thông tin của người dùng hiện tại dựa trên ngữ cảnh bảo mật.
 * Mỗi phương thức đều kiểm tra các điều kiện cần thiết và ném ApiException với mã lỗi tương ứng nếu có lỗi xảy ra,
 * chẳng hạn như người dùng không tồn tại hoặc người dùng chưa được xác thực.
 *
 */
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    // Inject UserRepository để tương tác với cơ sở dữ liệu
    @Autowired
    UserRepository userRepository;

    // Inject UserMapper để chuyển đổi giữa UserEntity và UserResponse
    @Autowired
    UserMapper userMapper;

    // Inject PasswordEncoder để mã hóa mật khẩu người dùng
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Phương thức getAllUsers trả về danh sách tất cả người dùng trong hệ thống.
     * Nó sử dụng userRepository để lấy tất cả UserEntity từ cơ sở dữ liệu,
     * sau đó sử dụng userMapper để chuyển đổi mỗi UserEntity thành UserResponse và trả về danh sách UserResponse.
     * @return List<UserResponse> danh sách tất cả người dùng dưới dạng UserResponse
     * @throws Exception nếu có lỗi trong quá trình lấy danh sách người dùng
     */
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    /**
     * Phương thức getUserByUserId trả về thông tin người dùng dựa trên userId.
     * Nó kiểm tra xem người dùng có tồn tại trong cơ sở dữ liệu hay không bằng
     * userRepository.findByUserId(user_id).isEmpty(). Nếu người dùng không tồn tại,
     * nó ném ApiException với mã lỗi USER_NOT_FOUND.
     * Nếu người dùng tồn tại, nó lấy UserEntity từ cơ sở dữ liệu, sử
     * dụng userMapper để chuyển đổi UserEntity thành UserResponse và trả về UserResponse đó.
     * @param user_id ID của người dùng cần lấy thông tin
     * @return UserResponse thông tin người dùng dưới dạng UserResponse
     * @throws Exception nếu người dùng không tồn tại hoặc có lỗi trong quá trình lấy thông tin người dùng
     */
    @Override
    public UserResponse getUserByUserId(String user_id) {
        // Kiểm tra xem người dùng có tồn tại trong cơ sở dữ liệu hay không
        if (userRepository.findByUserId(user_id).isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        // Lấy UserEntity từ cơ sở dữ liệu và chuyển đổi thành UserResponse để trả về
        Optional<UserEntity> userEntity = userRepository.findByUserId(user_id);
        return userMapper.toUserResponse(userEntity.get());
    }

    /**
     * Phương thức createUser tạo một người dùng mới dựa trên thông tin trong UserCreationRequest.
     * Nó kiểm tra xem userId đã tồn tại trong cơ sở dữ liệu hay chưa bằng
     * userRepository.findByUserId(userCreationRequest.userId()).isPresent(). Nếu userId đã tồn tại,
     * nó ném ApiException với mã lỗi USER_EXIST. Nếu userId chưa tồn tại, nó sử dụng userMapper để chuyển đổi
     * UserCreationRequest thành UserEntity, mã hóa mật khẩu nếu có, lưu UserEntity
     * vào cơ sở dữ liệu thông qua userRepository, sau đó chuyển đổi UserEntity thành UserResponse và trả về UserResponse đó.
     * @param userCreationRequest thông tin người dùng cần tạo dưới dạng UserCreationRequest
     * @return UserResponse thông tin người dùng đã tạo dưới dạng UserResponse
     * @throws Exception nếu userId đã tồn tại hoặc có lỗi trong quá trình tạo người dùng
     */
    @Override
    public UserResponse createUser(UserCreationRequest userCreationRequest) {
        // Lấy role từ UserCreationRequest, nếu role không được cung cấp thì mặc định là "user"
        String role =
                userCreationRequest.role() != null ? userCreationRequest.role().toLowerCase() : "user";

        // Lấy userId lớn nhất hiện tại trong cơ sở dữ liệu theo role để tạo userId mới cho người dùng mới
        String userId = userRepository.findMaxUserId(role.toUpperCase());
        int newUserId = 1; // Mặc định nếu chưa có người dùng nào

        // Nếu userId tồn tại, tách phần số và tăng lên 1 để tạo userId mới
        if (userId != null && !userId.isBlank()) {
            String numberPart = userId.replaceAll("[^0-9]", "");
            if (!numberPart.isBlank()) {
                newUserId = Integer.parseInt(numberPart) + 1;
            }
        }

        // Tạo userId mới theo định dạng "role + số thứ tự 3 chữ số", ví dụ: "user001", "admin002"
        String newUserIdStr = String.format("%s%03d", role, newUserId);

        // Chuyển đổi UserCreationRequest thành UserEntity, mã hóa mật khẩu nếu có, lưu vào cơ sở dữ liệu và trả về
        // UserResponse
        UserEntity userEntity = userMapper.toUserEntity(userCreationRequest);

        // Gán userId và password mới cho UserEntity
        userEntity.setUserId(newUserIdStr);
        userEntity.setPassword(
                userCreationRequest.password().isBlank()
                        ? null
                        : passwordEncoder.encode(userCreationRequest.password()));

        // Lưu UserEntity vào cơ sở dữ liệu thông qua userRepository
        userRepository.save(userEntity);
        // Chuyển đổi UserEntity thành UserResponse và trả về
        return userMapper.toUserResponse(userEntity);
    }

    /**
     * Phương thức updateUser cập nhật thông tin người dùng dựa trên user_id và thông tin trong UserUpdationRequest.
     * Nó kiểm tra xem người dùng có tồn tại trong cơ sở dữ liệu hay không bằng
     * userRepository.findByUserId(user_id).isEmpty(). Nếu người dùng không tồn tại,
     * nó ném ApiException với mã lỗi USER_NOT_FOUND. Nếu người dùng tồn tại, nó lấy UserEntity từ cơ sở dữ liệu,
     * cập nhật các trường thông tin người dùng nếu có trong UserUpdationRequest, lưu
     * UserEntity đã cập nhật vào cơ sở dữ liệu thông qua userRepository, sau đó chuyển đổi UserEntity thành UserResponse và trả về UserResponse đó.
     * @param user_id ID của người dùng cần cập nhật thông tin
     * @param userUpdationRequest thông tin người dùng cần cập nhật dưới dạng UserUpdationRequest
     * @return UserResponse thông tin người dùng đã cập nhật dưới dạng UserResponse
     * @throws Exception nếu người dùng không tồn tại hoặc có lỗi trong quá trình cập nhật thông tin người dùng
     */
    @Override
    public UserResponse updateUser(String user_id, UserUpdationRequest userUpdationRequest) {
        // Kiểm tra xem người dùng có tồn tại trong cơ sở dữ liệu hay không
        Optional<UserEntity> userResult = userRepository.findByUserId(user_id);
        // Nếu người dùng không tồn tại, ném ApiException với mã lỗi USER_NOT_FOUND
        if (userResult.isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        // Nếu người dùng tồn tại, lấy UserEntity từ cơ sở dữ liệu, cập nhật các trường thông tin người dùng nếu có
        // trong UserUpdationRequest,
        // lưu UserEntity đã cập nhật vào cơ sở dữ liệu thông qua userRepository, sau đó chuyển đổi UserEntity thành
        // UserResponse và trả về UserResponse đó
        UserEntity userEntity = userResult.get();
        if (userUpdationRequest.username().isPresent()) {
            userEntity.setUsername(userUpdationRequest.username().get());
        }

        if (userUpdationRequest.password().isPresent()) {
            userEntity.setPassword(
                    passwordEncoder.encode(userUpdationRequest.password().get()));
        }

        if (userUpdationRequest.role().isPresent()) {
            userEntity.setRole(userUpdationRequest.role().get());
        }

        // Lưu UserEntity đã cập nhật vào cơ sở dữ liệu thông qua userRepository
        userRepository.save(userEntity);
        // Chuyển đổi UserEntity thành UserResponse và trả về
        return userMapper.toUserResponse(userEntity);
    }

    /**
     * Phương thức getMyInfo trả về thông tin của người dùng hiện tại dựa trên ngữ cảnh bảo mật.
     * Nó kiểm tra xem ngữ cảnh bảo mật có hợp lệ và người dùng đã được xác thực hay chưa.
     * Nếu không, nó ném ApiException với mã lỗi USER_NOT_AUTHENTICATED.
     * Nếu người dùng đã được xác thực, nó lấy userId từ ngữ cảnh bảo mật,
     * kiểm tra xem người dùng có tồn tại trong cơ sở dữ liệu hay không bằng
     * userRepository.findByUserId(userId).isEmpty(). Nếu người dùng không tồn tại,
     * nó ném ApiException với mã lỗi USER_NOT_FOUND.
     * Nếu người dùng tồn tại, nó lấy UserEntity từ cơ sở dữ liệu,
     * sử dụng userMapper để chuyển đổi UserEntity thành UserResponse và trả về UserResponse đó.
     * @param user_id ID của người dùng cần lấy thông tin
     * @return UserResponse thông tin người dùng hiện tại dưới dạng UserResponse
     * @throws Exception nếu người dùng chưa được xác thực,
     * người dùng không tồn tại hoặc có lỗi trong quá trình lấy thông tin người dùng
     */
    @Override
    public UserResponse getMyInfo() {
        // Kiểm tra xem ngữ cảnh bảo mật có hợp lệ và người dùng đã được xác thực hay chưa
        var context = SecurityContextHolder.getContext();
        if (context == null
                || context.getAuthentication() == null
                || !context.getAuthentication().isAuthenticated()) {
            throw new ApiException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        // Nếu người dùng đã được xác thực, lấy userId từ ngữ cảnh bảo mật, kiểm tra xem người dùng có tồn tại trong cơ
        // sở dữ liệu hay không
        String userId = context.getAuthentication().getName();
        Optional<UserEntity> userEntity = userRepository.findByUserId(userId);

        // Nếu người dùng không tồn tại, ném ApiException với mã lỗi USER_NOT_FOUND
        if (userEntity.isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        // Nếu người dùng tồn tại, chuyển đổi UserEntity thành UserResponse và trả về
        return userMapper.toUserResponse(userEntity.get());
    }
}
