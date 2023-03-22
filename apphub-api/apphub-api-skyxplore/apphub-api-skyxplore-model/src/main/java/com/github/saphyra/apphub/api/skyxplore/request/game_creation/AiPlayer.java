package com.github.saphyra.apphub.api.skyxplore.request.game_creation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AiPlayer {
    private UUID userId;
    private String name;
    private UUID allianceId;
}
