package org.haridas.ezploy.integration;

import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.model.Project;
import org.haridas.ezploy.project.repo.ProjectRepository;
import org.haridas.ezploy.support.TestDataFactory;

import org.haridas.ezploy.support.TestDatabaseConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestDatabaseConfiguration.class)
public class ProjectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeAll
    static void setTimeZone() {
        TimeZone.setDefault(
                TimeZone.getTimeZone("Asia/Kolkata")
        );
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateProjectAndPersistIt() throws Exception {

        CreateProjectRequest request =
                TestDataFactory.createProjectRequest();

        MvcResult result = mockMvc.perform(
                        post("/api/v1/projects")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value(request.getName()))
                .andReturn();

        JsonNode json =
                objectMapper.readTree(result.getResponse().getContentAsString());

        UUID projectId =
                UUID.fromString(json.get("id").asString());

        Project savedProject =
                projectRepository.findById(projectId)
                        .orElseThrow();

        assertThat(savedProject.getName())
                .isEqualTo(request.getName());

        assertThat(savedProject.getDescription())
                .isEqualTo(request.getDescription());

        assertThat(savedProject.getFramework())
                .isEqualTo(request.getFramework());

        assertThat(savedProject.getRepositoryUrl())
                .isEqualTo(request.getRepositoryUrl());
    }

    @Test
    void shouldReturnProjectWhenProjectExists() throws Exception {

        Project project = TestDataFactory.project();

        projectRepository.save(project);

        mockMvc.perform(
                        get("/api/v1/projects/{id}", project.getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(project.getId().toString()))
                .andExpect(jsonPath("$.name")
                        .value(project.getName()))
                .andExpect(jsonPath("$.description")
                        .value(project.getDescription()))
                .andExpect(jsonPath("$.framework")
                        .value(project.getFramework().name()))
                .andExpect(jsonPath("$.repositoryUrl")
                        .value(project.getRepositoryUrl()));
    }

    @Test
    void shouldReturnNotFoundWhenProjectDoesNotExist()
            throws Exception {

        UUID projectId = UUID.randomUUID();

        mockMvc.perform(
                        get("/api/v1/projects/{id}", projectId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.errorCode")
                        .value("PROJECT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Project " + projectId + " not found"));
    }

    @Test
    void shouldUpdateProjectAndPersistChanges() throws Exception {

        Project project = TestDataFactory.project();
        projectRepository.save(project);

        UpdateProjectRequest request =
                TestDataFactory.updateProjectRequest();

        mockMvc.perform(
                        put("/api/v1/projects/{id}", project.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value(request.getName()));

        Project updatedProject =
                projectRepository.findById(project.getId())
                        .orElseThrow();

        assertThat(updatedProject.getName())
                .isEqualTo(request.getName());

        assertThat(updatedProject.getDescription())
                .isEqualTo(request.getDescription());

        assertThat(updatedProject.getFramework())
                .isEqualTo(request.getFramework());

        assertThat(updatedProject.getRepositoryUrl())
                .isEqualTo(request.getRepositoryUrl());
    }
    @Test
    void shouldDeleteProjectAndRemoveItFromDatabase()
            throws Exception {

        Project project = TestDataFactory.project();
        projectRepository.save(project);

        UUID projectId = project.getId();

        mockMvc.perform(
                        delete("/api/v1/projects/{id}", projectId)
                )
                .andExpect(status().isNoContent());

        assertThat(projectRepository.findById(projectId))
                .isEmpty();
    }
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingProject()
            throws Exception {

        UUID projectId = UUID.randomUUID();

        mockMvc.perform(
                        delete("/api/v1/projects/{id}", projectId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.errorCode")
                        .value("PROJECT_NOT_FOUND"))
                .andExpect(jsonPath("$.message")
                        .value("Project " + projectId + " not found"));
    }

//    @Test
//    void shouldStartWithEmptyDatabase() {
//
//        assertThat(projectRepository.count())
//                .isZero();
//    }

    @Test
    void shouldReturnPaginatedProjects() throws Exception {

        Project project1 = TestDataFactory.project();
        project1.setName("Project A");

        Project project2 = TestDataFactory.project();
        project2.setName("Project B");

        Project project3 = TestDataFactory.project();
        project3.setName("Project C");

        projectRepository.saveAll(
                List.of(project1, project2, project3)
        );

        mockMvc.perform(
                        get("/api/v1/projects")
                                .param("page", "0")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.projects.length()")
                        .value(2))
                .andExpect(jsonPath("$.pagination.currentPage")
                        .value(0))
                .andExpect(jsonPath("$.pagination.pageSize")
                        .value(2))
                .andExpect(jsonPath("$.pagination.totalElements")
                        .value(3))
                .andExpect(jsonPath("$.pagination.totalPages")
                        .value(2))
                .andExpect(jsonPath("$.pagination.first")
                        .value(true))
                .andExpect(jsonPath("$.pagination.last")
                        .value(false))
                .andExpect(jsonPath("$.pagination.hasNext")
                        .value(true));
    }
    @Test
    void shouldFilterProjectsByFramework() throws Exception {

        Project springProject = TestDataFactory.project();
        springProject.setName("Spring Project");
        springProject.setFramework(Framework.SPRING_BOOT);

        Project reactProject = TestDataFactory.project();
        reactProject.setName("React Project");
        reactProject.setFramework(Framework.REACT);

        projectRepository.saveAll(
                List.of(springProject, reactProject)
        );

        mockMvc.perform(
                        get("/api/v1/projects")
                                .param("framework", "SPRING_BOOT")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects.length()")
                        .value(1))
                .andExpect(jsonPath("$.projects[0].name")
                        .value("Spring Project"))
                .andExpect(jsonPath("$.pagination.totalElements")
                        .value(1));
    }
    @Test
    void shouldSortProjectsByNameAscending() throws Exception {

        Project projectB = TestDataFactory.project();
        projectB.setName("Project B");

        Project projectA = TestDataFactory.project();
        projectA.setName("Project A");

        Project projectC = TestDataFactory.project();
        projectC.setName("Project C");

        projectRepository.saveAll(
                List.of(projectB, projectA, projectC)
        );

        mockMvc.perform(
                        get("/api/v1/projects")
                                .param("sort", "name,asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects[0].name")
                        .value("Project A"))
                .andExpect(jsonPath("$.projects[1].name")
                        .value("Project B"))
                .andExpect(jsonPath("$.projects[2].name")
                        .value("Project C"));
    }
}
