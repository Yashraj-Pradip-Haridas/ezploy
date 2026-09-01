package org.haridas.ezploy.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 1, max = 50, message = "Username must be between 1 to 50 letters.")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, max = 50, message = "Password must be minimum 5 and maximum 50.")
    private String password;
}
