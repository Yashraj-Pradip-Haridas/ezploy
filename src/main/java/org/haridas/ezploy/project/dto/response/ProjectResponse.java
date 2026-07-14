package org.haridas.ezploy.project.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.haridas.ezploy.project.enums.Framework;

import java.time.OffsetDateTime;
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
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
