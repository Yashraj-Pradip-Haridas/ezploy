package org.haridas.ezploy.project.controller;

import jakarta.validation.Valid;
import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.request.UpdateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectResponse;
import org.haridas.ezploy.project.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.status(HttpStatus.OK).body(projectService.getAllProjects());
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest request) {

        return ResponseEntity.status(HttpStatus.OK).body(projectService.updateProject(id, request));
    }
}
