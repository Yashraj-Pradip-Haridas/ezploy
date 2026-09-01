package org.haridas.ezploy.project.controller;

import org.haridas.ezploy.common.exception.UserAlreadyExistsException;
import org.haridas.ezploy.project.dto.request.RegisterRequest;
import org.haridas.ezploy.project.dto.response.RegisterResponse;
import org.haridas.ezploy.project.model.User;
import org.haridas.ezploy.project.service.AuthService;
import org.haridas.ezploy.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUser() throws Exception {

        // Arrange
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");

        RegisterRequest registerRequest =
                TestDataFactory.registerRequest();

        RegisterResponse registerResponse =
                TestDataFactory.registerResponse(user);

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(registerResponse);

        // Act + Assert
        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username")
                        .value("username"));

        // Verify
        ArgumentCaptor<RegisterRequest> captor =
                ArgumentCaptor.forClass(RegisterRequest.class);

        verify(authService)
                .register(captor.capture());

        RegisterRequest captured =
                captor.getValue();

        assertThat(captured.getUsername())
                .isEqualTo(registerRequest.getUsername());

        assertThat(captured.getPassword())
                .isEqualTo(registerRequest.getPassword());

        verifyNoMoreInteractions(authService);
    }

    @Test
    void shouldReturnBadRequestWhenRegistrationRequestIsInvalid()
            throws Exception {
        RegisterRequest registerRequest =
                TestDataFactory.registerRequest();
        registerRequest.setUsername("");
        registerRequest.setPassword("");
        mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());

        verify(authService, never())
                .register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnConflictWhenUsernameAlreadyExists()
            throws Exception {
        RegisterRequest registerRequest =
                TestDataFactory.registerRequest();
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(
                        new UserAlreadyExistsException(
                                "User already exists with this username"
                        )
                );
        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        verify(authService)
                .register(any(RegisterRequest.class));
    }
}
