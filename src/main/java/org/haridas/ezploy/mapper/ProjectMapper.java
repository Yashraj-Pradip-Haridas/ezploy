package org.haridas.ezploy.mapper;

import org.haridas.ezploy.dto.response.ProjectResponse;
import org.haridas.ezploy.model.Project;
import org.springframework.stereotype.Component;
import org.haridas.ezploy.dto.request.CreateProjectRequest;
@Component
public class ProjectMapper {

    public Project toProject(CreateProjectRequest request){
        Project project = new Project();
        project.setName( request.getName());
        project.setDescription(request.getDescription());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setFramework(request.getFramework());
        return project;
    }

    public ProjectResponse toProjectResponse(Project project){

        ProjectResponse projectResponse =  new ProjectResponse();
        projectResponse.setId(project.getId());
        projectResponse.setName(project.getName());
        projectResponse.setDescription(project.getDescription());
        projectResponse.setRepositoryUrl(project.getRepositoryUrl());
        projectResponse.setFramework(project.getFramework());
        return projectResponse;
    }


}
