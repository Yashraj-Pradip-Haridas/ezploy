package org.haridas.ezploy.project.service;

import org.haridas.ezploy.common.exception.ProjectNotFoundException;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.mapper.ProjectMapper;
import org.haridas.ezploy.project.model.Project;
import org.haridas.ezploy.project.repo.ProjectRepository;
import org.haridas.ezploy.project.service.impl.ProjectServiceImpl;
import org.haridas.ezploy.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void shouldReturnProjectWhenProjectExists() {

        // Arrange
        UUID projectId = UUID.randomUUID();

        Project project = TestDataFactory.project();

        ProjectResponse response = TestDataFactory.projectResponse(project);

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

//        Arrange
        UUID projectId = UUID.randomUUID();

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
        CreateProjectRequest request = TestDataFactory.createProjectRequest();

        Project project = TestDataFactory.project();

        ProjectResponse response = TestDataFactory.projectResponse(project);

        when(projectMapper.toEntity(request)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toResponse(project)).thenReturn(response);

        ProjectResponse actualResponse = projectService.createProject(request);
        assertThat(actualResponse).isEqualTo(response);

        verify(projectMapper).toEntity(request);
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
                        request.getName(),
                        request.getDescription(),
                        request.getFramework(),
                        request.getRepositoryUrl()
                );

        InOrder inOrder =
                inOrder(projectMapper, projectRepository);

        inOrder.verify(projectMapper)
                .toEntity(request);

        inOrder.verify(projectRepository)
                .save(any(Project.class));

        inOrder.verify(projectMapper)
                .toResponse(project);

        verifyNoMoreInteractions(
                projectRepository,
                projectMapper
        );
    }

}
