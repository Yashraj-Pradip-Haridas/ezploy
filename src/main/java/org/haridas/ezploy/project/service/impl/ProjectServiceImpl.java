package org.haridas.ezploy.project.service.impl;

import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.mapper.ProjectMapper;
import org.haridas.ezploy.project.model.Project;
import org.haridas.ezploy.project.repo.ProjectRepository;
import org.haridas.ezploy.project.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest createProjectRequest){

        Project project = projectMapper.toEntity(createProjectRequest);

//        UUID Generation
        project.setId(UUID.randomUUID());
        project = projectRepository.save(project);
        return projectMapper.toResponse(project);
    }
}
