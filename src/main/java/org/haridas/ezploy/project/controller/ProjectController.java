package org.haridas.ezploy.project.controller;

import jakarta.validation.Valid;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectPageResponse;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.service.ProjectService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1")
@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(@Valid  @RequestBody CreateProjectRequest createProjectRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(createProjectRequest));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getProject(projectId));
    }

    @GetMapping("/projects")
    public ResponseEntity<ProjectPageResponse> getAllProjects(
            @RequestParam(required = false) Framework framework,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjects(framework, pageable));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(id, request));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {

        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
