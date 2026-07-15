package org.haridas.ezploy.project.service;

import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectPageResponse;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);
    ProjectResponse getProject(UUID projectId);
    ProjectPageResponse getAllProjects(Pageable pageable);
    ProjectResponse updateProject(UUID projectId,UpdateProjectRequest request);
    void deleteProject(UUID projectId);
}
