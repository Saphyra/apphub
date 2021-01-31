package com.github.saphyra.apphub.api.skyxplore.game.server;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface SkyXploreGameWebSocketEventController {
    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_GAME_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID from, @RequestBody WebSocketEvent event);

    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_USER_JOINED_TO_GAME)
    void userJoinedToGame(@PathVariable("userId") UUID userId);

    @DeleteMapping(Endpoints.INTERNAL_SKYXPLORE_USER_LEFT_GAME)
    void userLeftGame(@PathVariable("userId") UUID userId);
}