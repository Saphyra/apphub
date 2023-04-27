package com.github.saphyra.apphub.api.skyxplore.request.game_creation;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class SkyXploreGameCreationRequest {
    private UUID host;
    private Map<UUID, UUID> members; //<UserId, AllianceId>
    private List<AiPlayer> ais;
    private Map<UUID, String> alliances; //<AllianceId, AllianceName>
    private SkyXploreGameSettings settings;
    private String gameName;
}
