package org.haridas.ezploy.project.service;

import org.haridas.ezploy.common.exception.ProjectNotFoundException;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectPageResponse;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.mapper.ProjectMapper;
import org.haridas.ezploy.project.model.Project;
import org.haridas.ezploy.project.repo.ProjectRepository;
import org.haridas.ezploy.project.service.impl.ProjectServiceImpl;
import org.haridas.ezploy.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectResponse response;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        project = TestDataFactory.project();
        response = TestDataFactory.projectResponse(project);
        projectId = project.getId();
    }

    @Test
    void shouldReturnProjectWhenProjectExists() {

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project))
                .thenReturn(response);

        // Act
        ProjectResponse actual =
                projectService.getProject(projectId);

        // Assert
        assertThat(actual).isEqualTo(response);
        InOrder inOrder =
                inOrder(projectRepository, projectMapper);

        inOrder.verify(projectRepository)
                .findById(projectId);

        inOrder.verify(projectMapper)
                .toResponse(project);

        verify(projectMapper)
                .toResponse(project);

        verify(projectRepository)
                .findById(projectId);

        verifyNoMoreInteractions(
                projectRepository,
                projectMapper
        );

    }

    @Test
    void shouldThrowProjectNotFoundExceptionWhenProjectDoesNotExist() {

        when(projectRepository.findById(projectId))
        .thenReturn(Optional.empty());

//        Act and Assert
        ProjectNotFoundException ex = assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.getProject(projectId));

        assertThat(ex.getMessage())
                .isEqualTo("Project " + projectId + " not found");


        verify(projectRepository)
                .findById(projectId);

        verify(projectMapper, never())
                .toResponse(any());

        verifyNoMoreInteractions(
                projectRepository,
                projectMapper
        );

    }

    @Test
    void shouldCreateProject() {
        CreateProjectRequest createProjectRequest = TestDataFactory.createProjectRequest();
        when(projectMapper.toEntity(createProjectRequest)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse actualResponse = projectService.createProject(createProjectRequest);
        assertThat(actualResponse).isEqualTo(response);

        verify(projectMapper).toEntity(createProjectRequest);
        verify(projectMapper).toResponse(project);
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        Project savedProject = captor.getValue();

        assertThat(savedProject)
                .extracting(
                        Project::getName,
                        Project::getDescription,
                        Project::getFramework,
                        Project::getRepositoryUrl
                )
                .containsExactly(
                        createProjectRequest.getName(),
                        createProjectRequest.getDescription(),
                        createProjectRequest.getFramework(),
                        createProjectRequest.getRepositoryUrl()
                );

        InOrder inOrder =
                inOrder(projectMapper, projectRepository);

        inOrder.verify(projectMapper)
                .toEntity(createProjectRequest);

        inOrder.verify(projectRepository)
                .save(any(Project.class));

        inOrder.verify(projectMapper)
                .toResponse(project);

        verifyNoMoreInteractions(
                projectRepository,
                projectMapper
        );
    }

    @Test
    void shouldUpdateProject() {
        UpdateProjectRequest updateProjectRequest = TestDataFactory.updateProjectRequest();
        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(projectMapper.toResponse(project))
                .thenReturn(response);

        ProjectResponse actual =
                projectService.updateProject(projectId, updateProjectRequest);

        assertThat(actual).isEqualTo(response);

        verify(projectRepository).findById(projectId);
        verify(projectMapper).updateEntity(updateProjectRequest, project);
        verify(projectMapper).toResponse(project);

        verifyNoMoreInteractions(projectRepository, projectMapper);
    }

    @Test
    void shouldDeleteProjectWhenProjectExists(){
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        projectService.deleteProject(projectId);
        verify(projectRepository)
                .findById(projectId);

        verify(projectRepository)
                .delete(project);
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void shouldThrowProjectNotFoundExceptionWhenDeletingNonExistingProject(){
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
        assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.deleteProject(projectId)
        );
        verify(projectRepository)
                .findById(projectId);

        verify(projectRepository, never())
                .delete(any(Project.class));
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    void shouldReturnAllProjects() {

//        Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Page<Project> page = new PageImpl<>(List.of(project));

        ProjectPageResponse expected =
                TestDataFactory.projectPageResponse(page);

        when(projectRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        when(projectMapper.toPageResponse(page))
                .thenReturn(expected);

//        Act
        ProjectPageResponse actual =
                projectService.getAllProjects(null, null,pageable);

//        Assert
        assertThat(actual)
                .isEqualTo(expected);

        verify(projectRepository).findAll(any(Specification.class), eq(pageable));
        verify(projectMapper).toPageResponse(page);
        verifyNoMoreInteractions(projectRepository, projectMapper);

    }

}
