package com.github.saphyra.apphub.api.skyxplore.game.client;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "skyxplore-game-ws", url = "${serviceUrls.skyxploreGame}")
public interface SkyXploreGameWebSocketEventApiClient {
    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_GAME_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID from, @RequestBody WebSocketEvent event, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_USER_JOINED_TO_GAME)
    void userJoinedToGame(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(Endpoints.SKYXPLORE_INTERNAL_USER_LEFT_GAME)
    void userLeftGame(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
