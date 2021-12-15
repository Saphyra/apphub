package com.github.saphyra.apphub.integration.backend.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BanDetailsResponse {
    private UUID id;
    private String bannedRole;
    private Long expiration;
    private Boolean permanent;
    private String reason;
    private UUID bannedById;
    private String bannedByUsername;
    private String bannedByEmail;
}
