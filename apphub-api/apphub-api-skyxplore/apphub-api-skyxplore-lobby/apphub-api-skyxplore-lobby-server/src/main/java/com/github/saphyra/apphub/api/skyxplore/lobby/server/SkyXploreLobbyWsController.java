package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface SkyXploreLobbyWsController {
    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_LOBBY_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID from, @RequestBody WebSocketEvent event);

    @PutMapping(Endpoints.SKYXPLORE_INTERNAL_LOBBY_PLAYER_ONLINE)
    void playerOnline(@PathVariable("userId") UUID userId);

    @DeleteMapping(Endpoints.SKYXPLORE_INTERNAL_LOBBY_PLAYER_OFFLINE)
    void playerOffline(@PathVariable("userId") UUID userId);
}
