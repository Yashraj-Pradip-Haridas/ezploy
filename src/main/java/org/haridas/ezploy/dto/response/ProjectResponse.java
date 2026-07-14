package org.haridas.ezploy.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.haridas.ezploy.enums.Framework;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProjectResponse {

    private UUID id;
    private String name;
    private String description;
    private String repositoryUrl;
    private Framework framework;
}
