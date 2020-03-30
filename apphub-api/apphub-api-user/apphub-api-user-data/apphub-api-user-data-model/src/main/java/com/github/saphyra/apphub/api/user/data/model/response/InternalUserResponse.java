package com.github.saphyra.apphub.api.user.data.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InternalUserResponse {
    private UUID userId;
    private String email;
    private String username;
    private String passwordHash;
}
