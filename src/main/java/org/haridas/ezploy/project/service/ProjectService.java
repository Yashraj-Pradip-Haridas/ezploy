package org.haridas.ezploy.project.service;

import org.haridas.ezploy.project.dto.request.CreateProjectRequest;
import org.haridas.ezploy.project.dto.response.ProjectResponse;


public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

}
