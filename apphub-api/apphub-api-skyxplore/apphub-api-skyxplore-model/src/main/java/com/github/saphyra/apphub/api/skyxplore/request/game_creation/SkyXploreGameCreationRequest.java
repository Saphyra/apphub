package com.github.saphyra.apphub.api.skyxplore.request.game_creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SkyXploreGameCreationRequest {
    private UUID host;
    private Map<UUID, UUID> members; //<UserId, AllianceId>
    private Map<UUID, String> alliances;
    private SkyXploreGameCreationSettingsRequest settings;
}
