package com.github.saphyra.apphub.api.skyxplore.lobby.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface SkyXploreLobbyWsController {
    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_LOBBY_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID from, @RequestBody WebSocketEvent event);

    @PutMapping(Endpoints.INTERNAL_SKYXPLORE_LOBBY_PLAYER_ONLINE)
    void characterOnline(@PathVariable("userId") UUID userId);

    @DeleteMapping(Endpoints.INTERNAL_SKYXPLORE_LOBBY_PLAYER_OFFLINE)
    void characterOffline(@PathVariable("userId") UUID userId);
}
