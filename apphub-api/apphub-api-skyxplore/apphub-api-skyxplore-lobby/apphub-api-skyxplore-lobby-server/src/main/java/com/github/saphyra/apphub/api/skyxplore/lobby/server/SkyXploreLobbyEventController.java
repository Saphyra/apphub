package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.api.platform.event_gateway.model.request.SendEventRequest;
import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.lib.event.SkyXploreLobbyCleanupEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SkyXploreLobbyEventController {
    @PostMapping(Endpoints.SKYXPLORE_LOBBY_CLEANUP_EVENT)
    void cleanupExpiredLobbies(@RequestBody SendEventRequest<SkyXploreLobbyCleanupEvent> request);
}
