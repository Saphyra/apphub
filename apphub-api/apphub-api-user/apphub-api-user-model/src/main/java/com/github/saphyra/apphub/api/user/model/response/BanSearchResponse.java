package com.github.saphyra.apphub.api.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BanSearchResponse {
    private UUID userId;
    private String username;
    private String email;
    private List<String> bannedRoles;
    private Boolean markedForDeletion;
}
