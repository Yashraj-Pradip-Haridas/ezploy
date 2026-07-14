package org.haridas.ezploy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.haridas.ezploy.enums.Framework;
import java.time.OffsetDateTime;
import java.util.UUID;

/*
* When Hibernate fetches a row from your database,
* it uses Reflection to create an empty Java object first using the no-arg constructor.
* Then, it uses setters or direct field access to inject the column values into that
* object.
*/

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="project")
public class Project {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false)
    private String repositoryUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Framework framework;

    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @Column(name="updated_at")
    private OffsetDateTime updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now(); // Automatically sets timestamp
        this.updatedAt = OffsetDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

}
