package com.vu.api.service.Impl;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vu.api.DTO.request.AuthenticationRequest;
import com.vu.api.DTO.request.RefreshTokenRequest;
import com.vu.api.DTO.response.AuthenticationResponse;
import com.vu.api.DTO.response.LogoutResponse;
import com.vu.api.DTO.response.RefreshTokenResponse;
import com.vu.api.ErrorConfig.ApiException;
import com.vu.api.ErrorConfig.ErrorCode;
import com.vu.api.entity.BlackListTokenEntity;
import com.vu.api.entity.RefreshTokenEntity;
import com.vu.api.entity.UserEntity;
import com.vu.api.repository.BlackListTokenRepository;
import com.vu.api.repository.RefreshTokenRepository;
import com.vu.api.repository.UserRepository;
import com.vu.api.service.AuthenticationService;
import com.vu.api.service.RefreshTokenService;

import lombok.experimental.FieldDefaults;

/**
 * Class AuthenticationServiceImpl để triển khai các phương thức xác thực người dùng như tạo token,
 * xác thực token, đăng nhập, đăng xuất và làm mới token.
 * Sử dụng thư viện Nimbus JOSE + JWT để tạo và xác thực JWT token,
 * đồng thời sử dụng Redis để quản lý refresh token và blacklist token.
 */
@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthenticationServiceImpl implements AuthenticationService {
    // Tiêm các repository và service cần thiết để xử lý logic xác thực và quản lý tokenS
    @Autowired
    UserRepository userRepository;

    // Mật khẩu sẽ được mã hóa bằng PasswordEncoder để đảm bảo an toàn khi lưu trữ và so sánh mật khẩu
    @Autowired
    PasswordEncoder passwordEncoder;

    // RefreshTokenService để xử lý logic liên quan đến refresh token,
    // như tạo mới refresh token và xác thực refresh token
    @Autowired
    RefreshTokenService refreshTokenService;

    // BlackListTokenRepository để quản lý các token đã bị blacklist (ví dụ khi người dùng đăng xuất)
    // để đảm bảo rằng các token này không còn hiệu lực nữa
    @Autowired
    BlackListTokenRepository blackListTokenRepository;

    // RefreshTokenRepository để quản lý các refresh token được lưu trữ trong Redis,
    // bao gồm việc tạo mới, tìm kiếm và xóa refresh token
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    // Các giá trị cấu hình cho JWT được lấy từ application.properties thông qua @Value,
    // bao gồm SIGNER_KEY (khóa bí mật để ký JWT),
    // validDurationInSeconds (thời gian hiệu lực của JWT) và
    // refreshableDurationInDays (thời gian hiệu lực của refresh token)
    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    // validDurationInSeconds là thời gian hiệu lực của JWT token, tính bằng giây. Ví dụ: 3600 giây = 1 giờ
    @Value("${jwt.valid-duration}")
    Long validDurationInSeconds;

    // refreshableDurationInDays là thời gian hiệu lực của refresh token, tính bằng ngày. Ví dụ: 7 ngày
    @Value("${jwt.refreshable-duration}")
    Long refreshableDurationInDays;

    /**
     * Phương thức generateToken để tạo mới JWT token cho người dùng dựa trên thông tin của UserEntity.
     * Phương thức này sẽ xây dựng JWT với header, payload (claims) và ký
     * sử dụng thuật toán HS512 và khóa bí mật được cấu hình. Claims sẽ bao gồm thông tin như subject (userId), issuer, issueTime, expirationTime, jwtID và roles.
     * Nếu có lỗi xảy ra trong quá trình tạo token, phương thức sẽ ném ra RuntimeException.
     * @param user đối tượng UserEntity chứa thông tin của người dùng để tạo token
     * @throws IllegalStateException nếu SIGNER_KEY không được cấu hình hoặc rỗng
     * @throws IllegalArgumentException nếu SIGNER_KEY không hợp lệ (không phải Base
     *  64 hoặc quá ngắn cho HS512)
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình ký token
     * @return String là JWT token đã được tạo ra dưới dạng chuỗi
     */
    @Override
    public String generateToken(UserEntity user) {
        // Validate signer key
        if (SIGNER_KEY == null || SIGNER_KEY.isBlank()) {
            throw new IllegalStateException("JWT signer key is not configured (jwt.signerKey)");
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(SIGNER_KEY);
        } catch (IllegalArgumentException ex) {
            // If not valid Base64, fall back to using raw UTF-8 bytes (but still validate length)
            keyBytes = SIGNER_KEY.getBytes(StandardCharsets.UTF_8);
        }

        if (keyBytes.length < 64) { // HS512 requires 512-bit key (64 bytes)
            throw new IllegalArgumentException("JWT signer key is too short for HS512; need at least 64 bytes");
        }

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS512)
                .type(JOSEObjectType.JWT)
                .build();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId())
                .issuer("ecom.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now()
                        .plus(validDurationInSeconds, ChronoUnit.SECONDS)
                        .toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("roles", List.of(user.getRole()))
                .build();

        SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
        try {
            signedJWT.sign(new MACSigner(keyBytes));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Phương thức authenticate để xác thực người dùng dựa trên thông tin đăng nhập (userId và password) trong AuthenticationRequest.
     * Phương thức sẽ kiểm tra xem userId có tồn tại trong cơ sở dữ liệu hay không, sau đó so sánh mật khẩu đã mã hóa với mật khẩu được cung cấp. Nếu xác thực thành công, phương thức sẽ tạo mới JWT token và refresh token, sau đó trả về AuthenticationResponse chứa token, refresh token và trạng thái xác thực.
     * @param request đối tượng AuthenticationRequest chứa thông tin đăng nhập của người dùng (userId và password)
     * @throws ApiException nếu userId không tồn tại hoặc mật khẩu không hợp lệ
     * @return AuthenticationResponse chứa token, refresh token và trạng thái xác thực của người dùng
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Kiểm tra xem userId có tồn tại trong cơ sở dữ liệu hay không
        if (userRepository.findByUserId(request.userId()).isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. So sánh mật khẩu đã mã hóa với mật khẩu được cung cấp
        UserEntity user = userRepository.findByUserId(request.userId()).get();
        Boolean isAuthenticated = passwordEncoder.matches(request.password(), user.getPassword());

        // 3. Nếu xác thực thành công, tạo mới JWT token và refresh token, sau đó trả về AuthenticationResponse chứa
        // token, refresh token và trạng thái xác thực
        if (!isAuthenticated) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD);
        }

        // Tạo token và refresh token mới cho user đã xác thực thành công
        String token = generateToken(user);
        String refreshToken = refreshTokenService.generateRefreshToken(user.getUserId());

        // Trả về token, refresh token và trạng thái xác thực
        return new AuthenticationResponse(token, refreshToken, isAuthenticated);
    }

    /**
     * Phương thức logout để xử lý đăng xuất người dùng bằng cách kiểm tra và xóa refresh token khỏi Redis,
     * đồng thời đưa access token vào blacklist để đảm bảo rằng token này không còn hiệu lực nữa. Phương thức sẽ kiểm tra tính hợp lệ của access token, xác thực quyền sở hữu refresh token và thực hiện các bước cần thiết để đăng xuất người dùng.
     * @param token access token của người dùng cần đăng xuất, được lấy từ header Authorization
     * @param refreshToken refresh token của người dùng cần đăng xuất, được cung cấp trong body của request
     * @throws ApiException nếu access token hoặc refresh token không hợp lệ,
     * hoặc nếu có hành vi gian lận/rút trộm token của người khác
     * @return LogoutResponse chứa thông điệp về kết quả của quá trình đăng xuất
     */
    @Override
    public LogoutResponse logout(String token, String refreshToken) {
        // 1. Kiểm tra null/rỗng
        if (token == null || token.isBlank() || refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        // 2. Xử lý cắt chuỗi và giải mã Access Token
        try {
            // 2. Xử lý cắt chuỗi và giải mã Access Token
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            SignedJWT signedJWT = SignedJWT.parse(actualToken);

            // Lấy userId từ Access Token (Thằng đang gọi API)
            String userIdFromAccessToken = signedJWT.getJWTClaimsSet().getSubject();
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            // 3. KIỂM TRA REFRESH TOKEN TRÊN REDIS
            RefreshTokenEntity refreshTokenEntity = refreshTokenRepository
                    .findById(refreshToken)
                    .orElse(null); // Nếu không tìm thấy, có thể nó đã tự hết hạn và biến mất trước đó

            // Nếu tìm thấy refresh token trên Redis, tiếp tục kiểm tra quyền sở hữu và xóa nó đi
            if (refreshTokenEntity != null) {
                // KIỂM TRA QUYỀN SỞ HỮU: Thằng đòi logout có đúng là chủ của Refresh Token này không?
                if (!refreshTokenEntity.getUserId().equals(userIdFromAccessToken)) {
                    // Nếu không khớp, tức là có hành vi gian lận/rút trộm token của người khác
                    throw new ApiException(ErrorCode.USER_NOT_AUTHORIZED);
                }

                // Nếu khớp, danh chính ngôn thuận xóa nó đi
                refreshTokenRepository.delete(refreshTokenEntity);
            }

            // 4. Đưa Access Token vào blacklist (giữ nguyên logic cũ)
            long currentTime = new Date().getTime();
            long remainingTimeInSeconds = (expirationTime.getTime() - currentTime) / 1000;

            // Chỉ đưa vào blacklist nếu token còn thời gian hiệu lực, không cần thiết nếu token đã hết hạn
            if (remainingTimeInSeconds > 0) {
                // Lưu token vào blacklist với thời gian tồn tại
                // bằng thời gian còn lại của token để tự động xóa sau khi token hết hạn
                BlackListTokenEntity blackListToken = new BlackListTokenEntity();
                // Sử dụng actualToken đã được cắt bỏ "Bearer " để lưu vào blacklist
                blackListToken.setToken(actualToken);
                // Đặt thời gian tồn tại của token trong blacklist bằng thời gian còn lại của token
                blackListToken.setTimeToLive(remainingTimeInSeconds);
                // Lưu vào repository (Redis)
                blackListTokenRepository.save(blackListToken);
            }

            // 5. Trả về phản hồi đăng xuất thành công
            return new LogoutResponse("Logout successful");

        } catch (ParseException e) {
            // Nếu có lỗi khi phân tích token, trả về lỗi Unauthorized
            throw new ApiException(ErrorCode.INVALID_ACCESS_TOKEN);
        } catch (ApiException e) {
            // Ném lại các ApiException của mình (ví dụ lỗi UNAUTHORIZED ở trên)
            throw e;
        } catch (Exception e) {
            // Đề phòng các lỗi khác (ví dụ lỗi kết nối Redis, lỗi database...)
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Phương thức verifyAccessToken để xác thực JWT token bằng cách kiểm tra xem token có tồn tại trong blacklist hay không,
     * sau đó sử dụng thư viện Nimbus JOSE + JWT để phân tích và xác thực token
     * bằng cách kiểm tra chữ ký và thời gian hết hạn. Nếu token hợp lệ, phương thức sẽ trả về true;
     * nếu token không hợp lệ hoặc có lỗi xảy ra, phương thức sẽ trả về false.
     * @param token JWT token cần xác thực, có thể bao gồm tiền tố "Bearer " ở đầu
     * @return boolean true nếu token hợp lệ, false nếu token không hợp lệ hoặc có
     * lỗi xảy ra trong quá trình xác thực
     * @throws IllegalStateException nếu SIGNER_KEY không được cấu hình hoặc rỗng
     * @throws IllegalArgumentException nếu SIGNER_KEY không hợp lệ (không phải Base
     * 64 hoặc quá ngắn cho HS512)
     * @throws RuntimeException nếu có lỗi xảy ra trong quá trình xác thực token
     *@throws JOSEException nếu có lỗi xảy ra trong quá trình xác thực token
     */
    @Override
    public boolean verifyAccessToken(String token) {
        // Validate signer key
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Kiểm tra nếu token rỗng sau khi cắt bỏ "Bearer "
        if (token.isBlank()) {
            return false; // Token rỗng hoặc chỉ chứa "Bearer "
        }

        // Kiểm tra nếu token tồn tại trong blacklist
        if (blackListTokenRepository.existsById(token)) {
            return false; // Token đã bị blacklist
        }

        try {
            // Nếu token hợp lệ, phương thức sẽ trả về true;
            // nếu token không hợp lệ hoặc có lỗi xảy ra, phương thức sẽ trả về false.
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(Base64.getDecoder().decode(SIGNER_KEY));
            if (!signedJWT.verify(verifier)) {
                return false; // Token không hợp lệ về chữ ký
            }
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                return false; // Token đã hết hạn
            }
            return true; // Token còn hạn
        } catch (Exception e) {
            return false; // Token không hợp lệ hoặc lỗi khi phân tích
        }
    }

    /**
     * Phương thức refreshToken để làm mới JWT token bằng cách kiểm tra tính hợp lệ của refresh token,
     * @param request đối tượng RefreshTokenRequest chứa refresh token cần làm mới
     * @throws ApiException nếu refresh token không hợp lệ hoặc user liên kết với refresh token không tồn tại
     * @return RefreshTokenResponse chứa token mới và trạng thái xác thực của người dùng
     * @throws IllegalStateException nếu SIGNER_KEY không được cấu hình hoặc rỗng
     */
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        // Kiểm tra nếu refresh token không hợp lệ hoặc user liên kết với refresh token không tồn tại
        if (refreshTokenRepository.findById(request.refreshToken()).isEmpty()) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // Nếu refresh token hợp lệ, tạo token mới cho user liên kết với refresh token đó
        String userId =
                refreshTokenRepository.findById(request.refreshToken()).get().getUserId();

        // Kiểm tra xem userId có tồn tại trong cơ sở dữ liệu hay không
        if (userRepository.findByUserId(userId).isEmpty()) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }

        // Tạo token mới cho user liên kết với refresh token đó
        UserEntity user = userRepository.findByUserId(userId).get();
        String newAccessToken = generateToken(user);
        boolean isAuthenticated = true; // Nếu đã có refresh token hợp lệ thì chắc chắn user đã authenticate rồi

        // Trả về token mới và trạng thái xác thực của người dùng
        return new RefreshTokenResponse(newAccessToken, isAuthenticated);
    }
}
