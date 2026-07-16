package org.haridas.ezploy.support;

import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.model.Project;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class TestDataFactory {
    private static final String NAME = "Ezploy";
    private static final String DESCRIPTION = "PaaS";
    private static final String REPOSITORY =
            "https://github.com/test";
    private static final Framework FRAMEWORK =
            Framework.SPRING_BOOT;
    private TestDataFactory() {}

    public static Project project() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);
        project.setName(NAME);
        project.setDescription(DESCRIPTION);
        project.setFramework(FRAMEWORK);
        project.setRepositoryUrl(REPOSITORY);
        project.setCreatedAt(OffsetDateTime.now());
        project.setUpdatedAt(OffsetDateTime.now());
        return project;
    }
    public static CreateProjectRequest createProjectRequest() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName(NAME);
        createProjectRequest.setDescription(DESCRIPTION);
        createProjectRequest.setFramework(FRAMEWORK);
        createProjectRequest.setRepositoryUrl(REPOSITORY);
        return createProjectRequest;
    }
    public static ProjectResponse projectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getRepositoryUrl(),
                project.getFramework(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
