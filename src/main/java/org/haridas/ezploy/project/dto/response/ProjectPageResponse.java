package org.haridas.ezploy.project.dto.response;

import org.haridas.ezploy.common.dto.response.PaginationResponse;

import java.util.List;

public record ProjectPageResponse (

    List<ProjectResponse> projects,
    PaginationResponse pagination
){}
