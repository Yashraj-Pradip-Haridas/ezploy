package org.haridas.ezploy.project.controller;

import org.haridas.ezploy.common.exception.ProjectNotFoundException;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectPageResponse;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.model.Project;
import org.haridas.ezploy.project.service.ProjectService;
import org.haridas.ezploy.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void shouldReturnProjectWhenProjectExists() throws Exception {
//        Arrange
        Project project = TestDataFactory.project();

        UUID id = project.getId();

        ProjectResponse response =
                TestDataFactory.projectResponse(project);
        when(projectService.getProject(id))
                .thenReturn(response);

//        Act and assert

        mockMvc.perform(
                get("/api/v1/projects/{id}", id)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Ezploy"))
                .andExpect( jsonPath("$.framework")
                        .value("SPRING_BOOT"));

        verify(projectService)
                .getProject(id);

    }

    @Test
    void shouldCreateProject() throws Exception {
        Project project = TestDataFactory.project();
        CreateProjectRequest request = TestDataFactory.createProjectRequest();
        ProjectResponse response =
                TestDataFactory.projectResponse(project);

        when(projectService.createProject(any(CreateProjectRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                .value("Ezploy"))
                .andExpect(jsonPath("$.framework")
                        .value("SPRING_BOOT"))
                .andExpect(jsonPath("$.description")
                        .value("PaaS"))
                .andExpect(jsonPath("$.repositoryUrl")
                        .value("https://github.com/test"));

        ArgumentCaptor<CreateProjectRequest> captor =
                ArgumentCaptor.forClass(CreateProjectRequest.class);

        verify(projectService)
                .createProject(captor.capture());

        CreateProjectRequest captured = captor.getValue();

        assertThat(captured.getName())
                .isEqualTo(request.getName());

        assertThat(captured.getDescription())
                .isEqualTo(request.getDescription());

        assertThat(captured.getFramework())
                .isEqualTo(request.getFramework());

        assertThat(captured.getRepositoryUrl())
                .isEqualTo(request.getRepositoryUrl());
        verifyNoMoreInteractions(projectService);
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
//        Arrange
        CreateProjectRequest request = new CreateProjectRequest();

        request.setName("");
        request.setDescription("PaaS");
        request.setRepositoryUrl("");
        request.setFramework(null);

//      Act and Assert
        mockMvc.perform(
                post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))

                .andExpect(jsonPath("$.errorCode")
                        .value("VALIDATION_ERROR"))

                .andExpect(jsonPath("$.fieldErrors.name")
                        .exists())

                .andExpect(jsonPath("$.fieldErrors.repositoryUrl")
                        .exists())

                .andExpect(jsonPath("$.fieldErrors.framework")
                        .exists());

        verify(projectService, never())
                .createProject(any());
        verifyNoMoreInteractions(projectService);
    }
    @Test
    void shouldReturnNotFoundWhenProjectDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        when(projectService.getProject(id))
                .thenThrow(new ProjectNotFoundException(id));

        mockMvc.perform(get("/api/v1/projects/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode")
                        .value("PROJECT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Project " + id + " not found"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/projects/" + id));

        verify(projectService).getProject(id);
        verifyNoMoreInteractions(projectService);
    }
    @Test
    void shouldUpdateProject() throws Exception {

        Project project = TestDataFactory.project();

        UUID id = project.getId();

        UpdateProjectRequest request =
                TestDataFactory.updateProjectRequest();

        ProjectResponse response =
                TestDataFactory.projectResponse(project);

        when(projectService.updateProject(eq(id),
                any(UpdateProjectRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/projects/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.name")
                        .value("Ezploy"))
                .andExpect(jsonPath("$.framework")
                        .value("SPRING_BOOT"));

        ArgumentCaptor<UpdateProjectRequest> captor =
                ArgumentCaptor.forClass(UpdateProjectRequest.class);

        verify(projectService)
                .updateProject(eq(id), captor.capture());

        UpdateProjectRequest captured = captor.getValue();

        assertThat(captured.getName())
                .isEqualTo(request.getName());

        assertThat(captured.getDescription())
                .isEqualTo(request.getDescription());

        assertThat(captured.getFramework())
                .isEqualTo(request.getFramework());

        assertThat(captured.getRepositoryUrl())
                .isEqualTo(request.getRepositoryUrl());

        verifyNoMoreInteractions(projectService);
    }
    @Test
    void shouldReturnBadRequestWhenUpdatingInvalidProject()
            throws Exception {

        UUID id = UUID.randomUUID();

        UpdateProjectRequest request =
                new UpdateProjectRequest();

        request.setName("");
        request.setRepositoryUrl("");
        request.setFramework(null);

        mockMvc.perform(
                        put("/api/v1/projects/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.name")
                        .exists())
                .andExpect(jsonPath("$.fieldErrors.repositoryUrl")
                        .exists())
                .andExpect(jsonPath("$.fieldErrors.framework")
                        .exists());

        verify(projectService, never())
                .updateProject(any(), any());

        verifyNoMoreInteractions(projectService);
    }

    @Test
    void shouldDeleteProject() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(
                        delete("/api/v1/projects/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(projectService)
                .deleteProject(id);

        verifyNoMoreInteractions(projectService);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingProject()
            throws Exception {

        UUID id = UUID.randomUUID();

        doThrow(new ProjectNotFoundException(id))
                .when(projectService)
                .deleteProject(id);

        mockMvc.perform(
                        delete("/api/v1/projects/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode")
                        .value("PROJECT_NOT_FOUND"));

        verify(projectService)
                .deleteProject(id);

        verifyNoMoreInteractions(projectService);
    }
    @Test
    void shouldReturnAllProjects() throws Exception {

        Pageable pageable = PageRequest.of(0, 4);
        Project project = TestDataFactory.project();
        Page<Project> page = new PageImpl<>(
                List.of(project),
                pageable,
                1
        );

        ProjectPageResponse response =
                TestDataFactory.projectPageResponse(page);

        when(projectService.getAllProjects(
                any(),
                any(),
                any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/projects")
                                .param("page", "0")
                                .param("size", "4")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects[0].name")
                        .value("Ezploy"))
                .andExpect(jsonPath("$.pagination.currentPage")
                        .value(0))
                .andExpect(jsonPath("$.pagination.pageSize")
                        .value(4));

        verify(projectService)
                .getAllProjects(any(), any(), any(Pageable.class));

        verifyNoMoreInteractions(projectService);
    }

    @Test
    void shouldReturnProjectsFilteredByFramework()
            throws Exception {

        Page<Project> page =
                new PageImpl<>(List.of(TestDataFactory.project()));

        ProjectPageResponse response =
                TestDataFactory.projectPageResponse(page);

        when(projectService.getAllProjects(
                eq(Framework.SPRING_BOOT),
                any(),
                any(Pageable.class)))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/projects")
                                .param("framework", "SPRING_BOOT")
                )
                .andExpect(status().isOk());

        verify(projectService)
                .getAllProjects(
                       ArgumentMatchers.eq(Framework.SPRING_BOOT),
                        isNull(),
                        any(Pageable.class)
                );

        verifyNoMoreInteractions(projectService);
    }
    @Test
    void shouldReturnBadRequestWhenFrameworkIsInvalid()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/projects")
                                .param("framework", "INVALID")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message")
                        .exists());

        verifyNoInteractions(projectService);
    }

}