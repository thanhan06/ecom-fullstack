package com.vu.api.config;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vu.api.service.AuthenticationService;

/**
 * Class JwtAuthenticationFilter to filter JWT token from header and set authentication in Spring Security context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // BƯỚC 1: Tiêm AuthenticationService vào để dùng lại hàm verify
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Override doFilterInternal để thực hiện lọc JWT token từ header và set authentication vào Spring Security context
     * @param request đối tượng HttpServletRequest chứa thông tin của request
     * @param response đối tượng HttpServletResponse để gửi phản hồi
     * @param filterChain đối tượng FilterChain để tiếp tục luồng filter
     * @throws ServletException nếu có lỗi xảy ra trong quá trình lọc
     * @throws java.io.IOException nếu có lỗi I/O xảy ra trong quá trình lọc
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, java.io.IOException {

        // Lấy header Authorization
        String authorizationHeader = request.getHeader("Authorization");

        // Kiểm tra nếu header tồn tại và bắt đầu bằng "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            // BƯỚC 2: Gọi hàm verify chuẩn chỉnh của bạn ở đây
            // Hàm này đã tự check null, check blacklist, check chữ ký, check hạn rồi.
            if (authenticationService.verifyAccessToken(token)) {
                try {
                    // Vì token đã ĐẢM BẢO hợp lệ sau khi qua hàm verify của bạn,
                    // Filter chỉ cần parse thô để lấy userId và roles nạp vào Spring Security thôi.
                    SignedJWT signedJWT = SignedJWT.parse(token);
                    JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

                    // Lấy userId từ subject và roles từ claim "roles"
                    String userId = claimsSet.getSubject();
                    List<String> roles = claimsSet.getStringListClaim("roles");

                    // Chuyển roles thành SimpleGrantedAuthority để nạp vào Spring Security
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (roles != null) {
                        for (String role : roles) {
                            authorities.add(new SimpleGrantedAuthority(role));
                        }
                    }

                    // Tạo UserDetails giả với userId làm username, password rỗng (vì đã xác thực qua token), và
                    // authorities từ roles
                    UserDetails principal = User.withUsername(userId)
                            .password("")
                            .authorities(authorities)
                            .build();

                    // Tạo Authentication token và set vào SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);

                    // BƯỚC 3: Set authentication vào SecurityContext để Spring Security nhận diện được user đã xác thực
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (Exception e) {
                    // Đề phòng hy hữu có lỗi parse xảy ra
                    SecurityContextHolder.clearContext();
                }
            } else {
                // Nếu hàm verify của bạn trả về false (hết hạn/blacklist...), xóa sạch quyền truy cập
                SecurityContextHolder.clearContext();
            }
        }

        // Tiếp tục luồng filter
        filterChain.doFilter(request, response);
    }
}
