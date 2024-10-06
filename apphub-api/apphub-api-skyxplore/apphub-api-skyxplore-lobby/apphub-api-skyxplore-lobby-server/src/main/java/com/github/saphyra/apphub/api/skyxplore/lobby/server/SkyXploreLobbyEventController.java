package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.lib.config.common.endpoints.skyxplore.GenericSkyXploreEndpoints;
import org.springframework.web.bind.annotation.PostMapping;

public interface SkyXploreLobbyEventController {
    /**
     * Triggered by scheduler-service. Removing the abandoned lobbies to save up memory
     */
    @PostMapping(GenericSkyXploreEndpoints.EVENT_SKYXPLORE_LOBBY_CLEANUP)
    void cleanupExpiredLobbies();
}
