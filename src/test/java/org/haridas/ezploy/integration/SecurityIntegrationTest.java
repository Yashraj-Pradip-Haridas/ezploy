package org.haridas.ezploy.integration;

import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.LoginRequest;
import org.haridas.ezploy.project.dto.response.LoginResponse;
import org.haridas.ezploy.project.repo.UserRepository;
import org.haridas.ezploy.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }
    private String registerAndLogin() throws Exception {

        org.haridas.ezploy.project.dto.request.RegisterRequest registerRequest =
                TestDataFactory.registerRequest();

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(registerRequest)
                                )
                )
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(registerRequest)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        LoginResponse loginResponse =
                objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        LoginResponse.class
                );

        return loginResponse.getToken();
    }

    @Test
    void shouldRejectProtectedEndpointWithoutToken()
            throws Exception {

        CreateProjectRequest request =
                TestDataFactory.createProjectRequest();

        mockMvc.perform(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectInvalidJwt()
            throws Exception {

        CreateProjectRequest request =
                TestDataFactory.createProjectRequest();

        mockMvc.perform(
                        post("/api/v1/projects")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer completely-invalid-token"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isForbidden());
    }
    @Test
    void shouldAllowProtectedEndpointWithValidJwt()
            throws Exception {

        String token = registerAndLogin();

        CreateProjectRequest request =
                TestDataFactory.createProjectRequest();

        mockMvc.perform(
                        post("/api/v1/projects")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated());
    }
    @Test
    void shouldRejectLoginWithInvalidPassword()
            throws Exception {

        org.haridas.ezploy.project.dto.request.RegisterRequest registerRequest =
                TestDataFactory.registerRequest();

        mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                registerRequest
                                        )
                                )
                )
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setUsername(
                registerRequest.getUsername()
        );

        loginRequest.setPassword("wrong-password");

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                loginRequest
                                        )
                                )
                )
                .andExpect(status().isUnauthorized());
    }
}
