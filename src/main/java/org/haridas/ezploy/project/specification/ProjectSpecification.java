package org.haridas.ezploy.project.specification;

import org.haridas.ezploy.project.enums.Framework;
import org.haridas.ezploy.project.model.Project;
import org.springframework.data.jpa.domain.Specification;

public final class ProjectSpecification {

    private ProjectSpecification() {}

//    Later add all other specifications like createdAfter, createdBefore and all other
    public static Specification<Project> hasFramework(Framework framework){
        return (root, query, builder) ->
                builder.equal(
                        root.get("framework"),
                        framework
                );
    }

    public static Specification<Project> hasName(String name) {
        return (root, query, builder) ->
                builder.like(
                        builder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}
