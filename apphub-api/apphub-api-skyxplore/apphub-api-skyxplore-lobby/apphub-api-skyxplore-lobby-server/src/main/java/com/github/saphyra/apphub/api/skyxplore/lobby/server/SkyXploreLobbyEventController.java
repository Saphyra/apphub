package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;

public interface SkyXploreLobbyEventController {
    @PostMapping(Endpoints.SKYXPLORE_LOBBY_CLEANUP_EVENT)
    void cleanupExpiredLobbies();
}