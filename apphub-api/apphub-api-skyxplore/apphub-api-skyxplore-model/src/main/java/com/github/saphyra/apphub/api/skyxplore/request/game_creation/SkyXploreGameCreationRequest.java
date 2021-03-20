package com.github.saphyra.apphub.api.skyxplore.request.game_creation;

import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class SkyXploreGameCreationRequest {
    private UUID host;
    private Map<UUID, UUID> members; //<UserId, AllianceId>
    private Map<UUID, String> alliances;
    private SkyXploreGameCreationSettingsRequest settings;
    private String gameName;
}
