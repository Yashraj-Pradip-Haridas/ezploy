package org.haridas.ezploy.project.mapper;

import org.haridas.ezploy.common.dto.response.PaginationResponse;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectPageResponse;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;

import java.util.List;

@Component
public class ProjectMapper {

    public Project toEntity(CreateProjectRequest request){
        Project project = new Project();
        project.setName( request.getName());
        project.setDescription(request.getDescription());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setFramework(request.getFramework());
        return project;
    }

    public ProjectResponse toResponse(Project project){

        ProjectResponse projectResponse =  new ProjectResponse();
        projectResponse.setId(project.getId());
        projectResponse.setName(project.getName());
        projectResponse.setDescription(project.getDescription());
        projectResponse.setRepositoryUrl(project.getRepositoryUrl());
        projectResponse.setFramework(project.getFramework());
        projectResponse.setCreatedAt(project.getCreatedAt());
        projectResponse.setUpdatedAt(project.getUpdatedAt());
        return projectResponse;
    }

    public void updateEntity(UpdateProjectRequest request, Project project){
        project.setName( request.getName());
        project.setDescription(request.getDescription());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setFramework(request.getFramework());
    }

    public ProjectPageResponse toPageResponse(Page<Project> page){
        List<ProjectResponse> projectResponses = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        PaginationResponse pagination = new PaginationResponse(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getNumberOfElements(),
                page.hasNext(),
                page.hasPrevious());

        return new ProjectPageResponse(projectResponses, pagination);
    }


}
