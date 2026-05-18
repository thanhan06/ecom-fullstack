package com.vu.api.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import com.vu.api.common.ApiException;
import com.vu.api.user.DTO.request.UserCreateRequest;
import com.vu.api.user.DTO.response.UserResponse;
import com.vu.api.user.entity.Role;
import com.vu.api.user.entity.User;
import com.vu.api.user.mapper.UserMapper;
import com.vu.api.user.repository.RoleRepository;
import com.vu.api.user.repository.UserRepository;
import com.vu.api.user.repository.UserRoleRepository;

@TestPropertySource("/test.properties")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EntityManager em;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserCreateRequest createUserRequest;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob = LocalDate.of(1990, 1, 1);

    @BeforeEach
    void initData() {
        userService = new UserService(userRepository, roleRepository, userRoleRepository, userMapper, passwordEncoder);
        ReflectionTestUtils.setField(userService, "em", em);

        createUserRequest = new UserCreateRequest("testuser@gmail.com", "12345678", dob);
        userResponse = new UserResponse(1L, "testuser@gmail.com", dob, java.util.List.of());
        user = User.builder().id(1L).email("testuser@gmail.com").dob(dob).build();
    }

    @Test
    void createUser_validRequest_success() {
        // GIVEN
        Role role = Role.builder().id(1L).name("USER").build();
        when(roleRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(role));
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findByIdWithRoles(any())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any())).thenReturn(userResponse);
        // WHEN
        var response = userService.create(createUserRequest);

        // THEN
        Assertions.assertThat(response.id()).isEqualTo(1L);
        Assertions.assertThat(response.email()).isEqualTo("testuser@gmail.com");
    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        // GIVEN
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        // WHEN, THEN
        var exception = assertThrows(ApiException.class, () -> userService.create(createUserRequest));

        Assertions.assertThat(exception.getErrorCode().code()).isEqualTo("USER_EMAIL_EXISTS");
    }

    @Test
    void getMyInfo_validUser_success() {
        // GIVEN
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);
        SecurityContext securityContext = org.mockito.Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser@gmail.com");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmailWithRolesIgnoreCase(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any())).thenReturn(userResponse);

        // WHEN
        var response = userService.getMyInfo();

        // THEN
        Assertions.assertThat(response.id()).isEqualTo(1L);
        Assertions.assertThat(response.email()).isEqualTo("testuser@gmail.com");
    }
}
