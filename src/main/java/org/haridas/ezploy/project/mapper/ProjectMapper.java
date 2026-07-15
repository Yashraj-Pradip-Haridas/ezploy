package org.haridas.ezploy.project.mapper;

import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.model.Project;
import org.springframework.stereotype.Component;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
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


}
