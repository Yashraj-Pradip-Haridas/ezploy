package org.haridas.ezploy.project.service;

import org.haridas.ezploy.common.exception.ProjectNotFoundException;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.mapper.ProjectMapper;
import org.haridas.ezploy.project.model.Project;
import org.haridas.ezploy.project.repo.ProjectRepository;
import org.haridas.ezploy.project.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
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

        Project project = new Project();
        project.setId(projectId);
        project.setName("Ezploy");
        project.setDescription("PaaS");
        project.setFramework(Framework.SPRING_BOOT);
        project.setRepositoryUrl("https://github.com/test");
        project.setCreatedAt(OffsetDateTime.now());
        project.setUpdatedAt(OffsetDateTime.now());

        ProjectResponse response = new ProjectResponse();
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

}
