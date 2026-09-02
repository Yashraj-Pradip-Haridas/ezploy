package org.haridas.ezploy.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 50, message = "Username must not exceed 50 characters.")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, max = 50, message = "Password must be between 5 and 50 characters.")
    private String password;
}