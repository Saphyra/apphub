package com.github.saphyra.apphub.integration.structure.api.skyxplore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LobbyMemberResponse {
    private UUID userId;
    private String characterName;
    private LobbyMemberStatus status;
    private UUID allianceId;
    private Long createdAt;
}
