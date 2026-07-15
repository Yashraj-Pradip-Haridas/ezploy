package org.haridas.ezploy.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.haridas.ezploy.project.enums.Framework;


@Getter @Setter
@NoArgsConstructor
public class UpdateProjectRequest {

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank
    private String repositoryUrl;

    @Size(max = 200)
    private String description;

    @NotNull
    private Framework framework;

}
