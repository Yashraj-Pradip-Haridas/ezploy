package org.haridas.ezploy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.haridas.ezploy.enums.Framework;

@Getter
@Setter
@NoArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 50, message = "Name must be in 2-50 characters")
    private String name;

    @NotBlank(message = "Repository URL cannot be empty")
    private String repositoryUrl;

    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;

    @NotNull
    private Framework framework;
}
