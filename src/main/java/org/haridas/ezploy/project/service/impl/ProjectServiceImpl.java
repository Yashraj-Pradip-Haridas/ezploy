package org.haridas.ezploy.project.service.impl;

import org.haridas.ezploy.common.exception.ProjectNotFoundException;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectPageResponse;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.mapper.ProjectMapper;
import org.haridas.ezploy.project.model.Project;
import org.haridas.ezploy.project.repo.ProjectRepository;
import org.haridas.ezploy.project.service.ProjectService;
import org.haridas.ezploy.project.specification.ProjectSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Override
    public ProjectResponse getProject(UUID projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new ProjectNotFoundException(projectId));
        return projectMapper.toResponse(project);
    }

    @Override
    public ProjectPageResponse getAllProjects(Framework framework,String name, Pageable pageable) {
        Specification<Project> spec = Specification.allOf();
        if(framework != null){
            spec = spec.and(ProjectSpecification.hasFramework(framework));
        }
        if(name != null && !name.isBlank()){
            spec = spec.and(ProjectSpecification.hasName(name));
        }

        Page<Project> page = projectRepository.findAll(spec, pageable);
         return projectMapper.toPageResponse(page);
    }

    @Override
    public ProjectResponse updateProject(UUID projectId, UpdateProjectRequest request){
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        projectMapper.updateEntity(request, existingProject);
        existingProject = projectRepository.save(existingProject);
        return projectMapper.toResponse(existingProject);
    }

    @Override
    public void deleteProject(UUID projectId) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        projectRepository.delete(existingProject);
    }
}
