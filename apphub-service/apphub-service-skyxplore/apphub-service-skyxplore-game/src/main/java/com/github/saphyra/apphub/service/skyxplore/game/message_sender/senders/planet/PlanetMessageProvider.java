package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.service.skyxplore.game.ws.planet.WsSessionPlanetMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanetMessageProvider {
    void clearDisconnectedUserData(List<WsSessionPlanetMapping> connectedUsers);

    Optional<PlanetUpdateItem> getMessage(String sessionId, UUID userId, UUID planetId);
}
