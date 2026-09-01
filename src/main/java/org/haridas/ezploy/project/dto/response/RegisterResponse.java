package org.haridas.ezploy.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private UUID id;
    private String username;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public RegisterResponse(UUID id, String username) {
        this.id = id;
        this.username = username;
    }
}
