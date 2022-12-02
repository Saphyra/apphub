package com.github.saphyra.apphub.integration.structure.user;

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
public class BanResponse {
    private UUID userId;
    private String username;
    private String email;
    private List<BanDetailsResponse> bans;
    private Boolean markedForDeletion;
    private Long markedForDeletionAt;
}
