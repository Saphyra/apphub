package com.github.saphyra.apphub.service.skyxplore.game.ws.etc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class WsSessionPlanetIdMapping implements WsSessionIdProvider {
    private final String sessionId;
    private final UUID planetId;
    private final UUID userId;
}
