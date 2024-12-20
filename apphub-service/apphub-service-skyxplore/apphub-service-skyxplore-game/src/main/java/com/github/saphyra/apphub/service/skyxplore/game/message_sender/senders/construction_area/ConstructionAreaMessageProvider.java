package com.github.saphyra.apphub.service.skyxplore.game.message_sender.senders.construction_area;

import com.github.saphyra.apphub.service.skyxplore.game.message_sender.UpdateItem;
import com.github.saphyra.apphub.service.skyxplore.game.ws.etc.WsSessionConstructionAreaIdMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ConstructionAreaMessageProvider {
    void clearDisconnectedUserData(List<WsSessionConstructionAreaIdMapping> connectedUsers);

    Optional<UpdateItem> getMessage(String sessionId, UUID userId, UUID planetId);
}
