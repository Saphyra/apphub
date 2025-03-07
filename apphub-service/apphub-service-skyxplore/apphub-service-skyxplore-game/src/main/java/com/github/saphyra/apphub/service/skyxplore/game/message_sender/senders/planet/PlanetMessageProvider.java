package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.planet;

import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionPlanetIdMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface PlanetMessageProvider {
    void clearDisconnectedUserData(List<WsSessionPlanetIdMapping> connectedUsers);

    Optional<UpdateItem> getMessage(String sessionId, UUID userId, UUID planetId);
}
