package com.github.saphyra.apphub.api.user.model.ban;

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
    private String expiration;
    private Boolean permanent;
    private String reason;
    private UUID bannedById;
    private String bannedByUsername;
    private String bannedByEmail;
}
