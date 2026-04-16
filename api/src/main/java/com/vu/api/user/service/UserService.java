package com.vu.api.user.service;

import com.vu.api.common.ApiException;
import com.vu.api.common.ErrorCode;
import com.vu.api.user.DTO.request.UserCreateRequest;
import com.vu.api.user.DTO.response.UserResponse;
import com.vu.api.user.entity.Role;
import com.vu.api.user.entity.User;
import com.vu.api.user.entity.UserRole;
import com.vu.api.user.mapper.UserMapper;
import com.vu.api.user.repository.RoleRepository;
import com.vu.api.user.repository.UserRepository;
import com.vu.api.user.repository.UserRoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @PersistenceContext
    private EntityManager em;

    public UserService(
            UserRepository userRepo,
            RoleRepository roleRepo,
            UserRoleRepository userRoleRepo,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse create(UserCreateRequest req) {
        log.info("Creating user with email: {}", req.email());
        if (userRepo.existsByEmailIgnoreCase(req.email())) {
            throw new ApiException(ErrorCode.USER_EMAIL_EXISTS);
        }

        User u = User.builder()
                .email(req.email())
                // TODO: thay bằng password encoder (BCrypt)
                .passwordHash(passwordEncoder.encode(req.password()))
                .dob(req.dob())
                .build();

        u = userRepo.save(u);

        // role mặc định là USER
        Role role = roleRepo.findByNameIgnoreCase("USER")
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));

        userRoleRepo.save(UserRole.builder().user(u).role(role).build());

        // đảm bảo insert xong + tránh Hibernate trả entity/cache cũ
        userRoleRepo.flush();
        em.clear();

        User refreshed = userRepo.findByIdWithRoles(u.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(refreshed);
    }

    @Transactional(readOnly = true)
    public UserResponse getMyInfo(){
         var context = SecurityContextHolder.getContext();
         String email = context.getAuthentication().getName();

        User u = userRepo.findByEmailWithRolesIgnoreCase(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(u);
    }

    @PostAuthorize(" returnObject.email == authentication.name or hasRole('ADMIN') ") // USER chỉ được xem thông tin của chính mình, ADMIN được xem tất cả
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User u = userRepo.findByIdWithRoles(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(u);
    }

    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can access this endpoint
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepo.findAllWithRoles();
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse assignRole(Long userId, String roleName) {
        userRepo.findByIdWithRoles(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepo.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));

        if (userRoleRepo.existsByUserIdAndRoleId(userId, role.getId())) {
            throw new ApiException(ErrorCode.USER_ROLE_EXISTS);
        }

        // user entity không cần giữ reference ở đây vì ta reload lại để trả response
        User userRef = User.builder().id(userId).build();
        userRoleRepo.save(UserRole.builder().user(userRef).role(role).build());

        userRoleRepo.flush();
        em.clear();

        User refreshed = userRepo.findByIdWithRoles(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(refreshed);
    }

    @Transactional
    public UserResponse removeRole(Long userId, String roleName) {
        userRepo.findByIdWithRoles(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepo.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));

        userRoleRepo.deleteByUserIdAndRoleId(userId, role.getId());

        userRoleRepo.flush();
        em.clear();

        User refreshed = userRepo.findByIdWithRoles(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(refreshed);
    }

    @Transactional
    public UserResponse update(Long id, com.vu.api.user.DTO.request.UserUpdateRequest req) {
        User u = userRepo.findByIdWithRoles(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (req.email() != null && !req.email().isBlank() && !req.email().equalsIgnoreCase(u.getEmail())) {
            if (userRepo.existsByEmailIgnoreCase(req.email())) {
                throw new ApiException(ErrorCode.USER_EMAIL_EXISTS);
            }
            u.setEmail(req.email());
        }

        if (req.password() != null && !req.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(req.password()));
        }

        if (req.dob() != null) {
            u.setDob(req.dob());
        }

        if (req.roleNames() != null) {
            userRoleRepo.deleteByUserId(u.getId());
            userRoleRepo.flush();
            em.clear();
            
            u = userRepo.findById(id).orElseThrow();
            
            for (String roleName : req.roleNames()) {
                Role role = roleRepo.findByNameIgnoreCase(roleName)
                        .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));
                userRoleRepo.save(UserRole.builder().user(u).role(role).build());
            }
        }

        userRepo.save(u);
        userRoleRepo.flush();
        em.clear();

        User refreshed = userRepo.findByIdWithRoles(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(refreshed);
    }

}