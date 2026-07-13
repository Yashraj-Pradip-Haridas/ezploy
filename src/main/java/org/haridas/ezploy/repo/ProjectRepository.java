package org.haridas.ezploy.repo;


import org.haridas.ezploy.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// Spring generates the implementation at runtime. So, we use interface
// For Spring Data JPA repositories, @Repository is optional.
// Spring automatically detects interfaces extending JpaRepository.
public interface ProjectRepository extends JpaRepository<Project, UUID> {

}
