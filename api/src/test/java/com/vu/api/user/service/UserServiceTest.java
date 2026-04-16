package com.vu.api.user.service;

import com.vu.api.user.DTO.request.UserCreateRequest;
import com.vu.api.user.DTO.response.UserResponse;
import com.vu.api.user.entity.User;
import com.vu.api.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vu.api.user.repository.RoleRepository;
import com.vu.api.user.entity.Role;
import com.vu.api.common.ApiException;
import com.vu.api.common.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.vu.api.user.repository.UserRoleRepository;
import com.vu.api.user.mapper.UserMapper;
import jakarta.persistence.EntityManager;

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
    private UserCreateRequest createUserRequest ;
    private UserResponse userResponse;
    private User user;
    private LocalDate dob = LocalDate.of(1990, 1, 1);
    @BeforeEach
    void initData(){
        userService = new UserService(userRepository, roleRepository, userRoleRepository, userMapper, passwordEncoder);
        ReflectionTestUtils.setField(userService, "em", em);

        createUserRequest = new UserCreateRequest(
                "testuser@gmail.com",
                "12345678",
                dob
        );
        userResponse = new UserResponse(
                1L,
                "testuser@gmail.com",
                dob,
                java.util.List.of()
        );
        user = User.builder()
                .id(1L)
                .email("testuser@gmail.com")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() {
        //GIVEN
        Role role = Role.builder().id(1L).name("USER").build();
        when(roleRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(role));
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findByIdWithRoles(any())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any())).thenReturn(userResponse);
        //WHEN
        var response = userService.create(createUserRequest);
        
        //THEN
        Assertions.assertThat(response.id()).isEqualTo(1L);
        Assertions.assertThat(response.email()).isEqualTo("testuser@gmail.com");


    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        //GIVEN
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        //WHEN, THEN
        var exception = assertThrows(ApiException.class, () -> userService.create(createUserRequest));

        Assertions.assertThat(exception.getErrorCode().code()).isEqualTo("USER_EMAIL_EXISTS");
    }
}
