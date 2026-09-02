package org.haridas.ezploy.project.service.impl;

import org.haridas.ezploy.common.exception.UserAlreadyExistsException;
import org.haridas.ezploy.project.dto.request.LoginRequest;
import org.haridas.ezploy.project.dto.request.RegisterRequest;
import org.haridas.ezploy.project.dto.response.LoginResponse;
import org.haridas.ezploy.project.dto.response.RegisterResponse;
import org.haridas.ezploy.project.enums.Role;
import org.haridas.ezploy.project.model.User;
import org.haridas.ezploy.project.repo.UserRepository;
import org.haridas.ezploy.project.security.JwtService;
import org.haridas.ezploy.project.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterRequest request){
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("User already exists with this username");
        }
        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);
        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
    }

    @Override
    public LoginResponse login(LoginRequest request){
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );
        String token = jwtService.generateToken(request.getUsername());
        return new LoginResponse(token);
    }


}
