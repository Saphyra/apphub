package com.github.saphyra.apphub.api.skyxplore.request.game_creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkyXploreLoadGameRequest {
    private UUID host;
    private UUID gameId;
    private List<UUID> members;
}
