package com.github.saphyra.apphub.service.skyxplore.game.ws.planet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class WsSessionPlanetMapping {
    private final String sessionId;
    private final UUID planetId;
    private final UUID userId;
}
