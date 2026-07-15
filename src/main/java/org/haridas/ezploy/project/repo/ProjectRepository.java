package org.haridas.ezploy.project.repo;


import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

// Spring generates the implementation at runtime. So, we use interface
// For Spring Data JPA repositories, @Repository is optional.
// Spring automatically detects interfaces extending JpaRepository.
public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {


}
