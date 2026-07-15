package org.haridas.ezploy.project.service;

import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectPageResponse;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.enums.Framework;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);
    ProjectResponse getProject(UUID projectId);
    ProjectPageResponse getAllProjects(Framework framework,String name, Pageable pageable);
    ProjectResponse updateProject(UUID projectId,UpdateProjectRequest request);
    void deleteProject(UUID projectId);
}
