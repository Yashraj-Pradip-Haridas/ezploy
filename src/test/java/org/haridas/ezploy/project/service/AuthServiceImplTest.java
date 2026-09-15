package org.haridas.ezploy.project.service;

import org.haridas.ezploy.common.exception.UserAlreadyExistsException;
import org.haridas.ezploy.project.dto.request.LoginRequest;
import org.haridas.ezploy.project.dto.request.RegisterRequest;
import org.haridas.ezploy.project.dto.response.LoginResponse;
import org.haridas.ezploy.project.dto.response.RegisterResponse;
import org.haridas.ezploy.project.enums.Role;
import org.haridas.ezploy.project.model.User;
import org.haridas.ezploy.project.repo.UserRepository;
import org.haridas.ezploy.project.security.JwtService;
import org.haridas.ezploy.project.service.impl.AuthServiceImpl;
import org.haridas.ezploy.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Test
    void shouldCreateUser() {
        RegisterRequest registerRequest =
                TestDataFactory.registerRequest();

        when(userRepository.findByUsername(
                registerRequest.getUsername()
        )).thenReturn(Optional.empty());

        when(passwordEncoder.encode(
                registerRequest.getPassword()
        )).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponse actual =
                authService.register(registerRequest);

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertThat(savedUser.getUsername())
                .isEqualTo(registerRequest.getUsername());

        assertThat(savedUser.getPassword())
                .isEqualTo("encodedPassword");

        assertThat(savedUser.getRole())
                .isEqualTo(Role.USER);
        verify(userRepository)
                .findByUsername(registerRequest.getUsername());
        verify(passwordEncoder)
                .encode(registerRequest.getPassword());
        assertThat(actual.getUsername())
                .isEqualTo(registerRequest.getUsername());
        assertThat(actual.getId())
                .isEqualTo(savedUser.getId());

        assertThat(actual.getCreatedAt())
                .isEqualTo(savedUser.getCreatedAt());

        assertThat(actual.getUpdatedAt())
                .isEqualTo(savedUser.getUpdatedAt());
        verifyNoMoreInteractions(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        RegisterRequest registerRequest =
                TestDataFactory.registerRequest();

        User existingUser = new User();
        existingUser.setUsername(registerRequest.getUsername());

        when(userRepository.findByUsername(
                registerRequest.getUsername()
        )).thenReturn(Optional.of(existingUser));
        assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(registerRequest)
        );
//        assertThatThrownBy(() -> authService.register(registerRequest))
//                .isInstanceOf(UserAlreadyExistsException.class)
//                .hasMessage("User already exists with this username");
        verify(userRepository)
                .findByUsername(registerRequest.getUsername());

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void shouldLogin(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(new UsernamePasswordAuthenticationToken("username", null));

        when(jwtService.generateToken("username"))
                .thenReturn("jwt-token");
        LoginResponse actual = authService.login(loginRequest);
        assertThat(actual.getToken()).isEqualTo("jwt-token");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

        verify(authenticationManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken token = captor.getValue();

        assertThat(token.getPrincipal()).isEqualTo("username");
        assertThat(token.getCredentials()).isEqualTo("password");

        verifyNoMoreInteractions(
                authenticationManager
        );
    }

//    Write pending tests for invalid request and bad credentials

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "username",
                        null
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(jwtService.generateToken("username"))
                .thenReturn("jwt-token");

        LoginResponse actual = authService.login(loginRequest);

        assertThat(actual.getToken()).isEqualTo("jwt-token");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken token =
                captor.getValue();

        assertThat(token.getPrincipal()).isEqualTo("username");
        assertThat(token.getCredentials()).isEqualTo("password");

        verify(jwtService).generateToken("username");

        verifyNoMoreInteractions(authenticationManager, jwtService);
    }

    @Test
    void shouldThrowExceptionWhenCredentialsAreInvalid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("wrongPassword");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException("Invalid username or password")
        );

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(jwtService, never()).generateToken(anyString());

        verifyNoMoreInteractions(authenticationManager, jwtService);
    }

}
