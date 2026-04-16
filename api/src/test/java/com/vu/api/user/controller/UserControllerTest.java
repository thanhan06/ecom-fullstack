package com.vu.api.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vu.api.user.DTO.request.UserCreateRequest;
import com.vu.api.user.DTO.response.UserResponse;
import com.vu.api.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserCreateRequest createUserRequest ;
    private UserResponse userResponse;
    private LocalDate dob = LocalDate.of(1990, 1, 1);
    @BeforeEach
    void initData(){
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
    }

    @Test
    // @DisplayName("Test for create user with valid request")
    void createUser_validRequest_success() throws Exception {
        //GIVEN
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(createUserRequest);

        Mockito.when(userService.create(ArgumentMatchers.any()))
                        .thenReturn(userResponse);
        //WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(201))
                .andExpect(MockMvcResultMatchers.jsonPath("data.id").value(1));



    }

    @Test
    void createUser_invalidEmail_badRequest() throws Exception {
        //GIVEN
        objectMapper.registerModule(new JavaTimeModule());
        createUserRequest = new UserCreateRequest(
                "invalid-email",
                "12345678",
                dob
        );
        String content = objectMapper.writeValueAsString(createUserRequest);

        //WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Validation failed"));
    }
}
