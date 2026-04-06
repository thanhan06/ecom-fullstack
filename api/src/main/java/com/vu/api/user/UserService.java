package com.vu.api.user;

import com.vu.api.common.ApiException;
import com.vu.api.common.ErrorCode;
import com.vu.api.user.DTO.UserCreateRequest;
import com.vu.api.user.DTO.UserResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final UserRoleRepository userRoleRepo;
    private final UserMapper userMapper;

    @PersistenceContext
    private EntityManager em;

    public UserService(
            UserRepository userRepo,
            RoleRepository roleRepo,
            UserRoleRepository userRoleRepo,
            UserMapper userMapper
    ) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.userRoleRepo = userRoleRepo;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse create(UserCreateRequest req) {
        if (userRepo.existsByEmailIgnoreCase(req.email())) {
            throw new ApiException(ErrorCode.USER_EMAIL_EXISTS);
        }

        User u = User.builder()
                .email(req.email())
                // TODO: thay bằng password encoder (BCrypt)
                .passwordHash(req.password())
                .build();

        u = userRepo.save(u);

        // roles: nếu null/empty thì mặc định USER
        List<String> roleNames = (req.roles() == null || req.roles().isEmpty())
                ? List.of("USER")
                : req.roles();

        for (String roleName : roleNames) {
            Role role = roleRepo.findByNameIgnoreCase(roleName)
                    .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NOT_FOUND));

            if (!userRoleRepo.existsByUserIdAndRoleId(u.getId(), role.getId())) {
                userRoleRepo.save(UserRole.builder().user(u).role(role).build());
            }
        }

        // đảm bảo insert xong + tránh Hibernate trả entity/cache cũ
        userRoleRepo.flush();
        em.clear();

        User refreshed = userRepo.findByIdWithRoles(u.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(refreshed);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User u = userRepo.findByIdWithRoles(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(u);
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
}