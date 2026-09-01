package org.haridas.ezploy.project.service;

import org.haridas.ezploy.project.dto.request.RegisterRequest;
import org.haridas.ezploy.project.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

}
