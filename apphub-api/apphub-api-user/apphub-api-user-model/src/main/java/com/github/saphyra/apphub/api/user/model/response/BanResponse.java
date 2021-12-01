package com.github.saphyra.apphub.api.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BanResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private String email;
    private String bannedRole;
    private Long expiration;
    private Boolean permanent;
    private String reason;
    private UUID bannedById;
    private String bannedByUsername;
    private String bannedByEmail;
}
