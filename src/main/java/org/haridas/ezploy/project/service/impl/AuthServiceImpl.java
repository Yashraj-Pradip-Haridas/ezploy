package org.haridas.ezploy.project.service.impl;

import org.haridas.ezploy.common.exception.UserAlreadyExistsException;
import org.haridas.ezploy.project.dto.request.RegisterRequest;
import org.haridas.ezploy.project.dto.response.RegisterResponse;
import org.haridas.ezploy.project.enums.Role;
import org.haridas.ezploy.project.model.User;
import org.haridas.ezploy.project.repo.UserRepository;
import org.haridas.ezploy.project.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    final private UserRepository userRepository;
    final private PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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


}
