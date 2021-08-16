package com.github.saphyra.apphub.api.skyxplore.lobby.client;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("skyxplore-lobby-ws")
public interface SkyXploreLobbyWsApiClient {
    @PostMapping(Endpoints.SKYXPLORE_INTERNAL_LOBBY_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID from, @RequestBody WebSocketEvent event, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PutMapping(Endpoints.SKYXPLORE_INTERNAL_LOBBY_PLAYER_ONLINE)
    void playerOnline(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @DeleteMapping(Endpoints.SKYXPLORE_INTERNAL_LOBBY_PLAYER_OFFLINE)
    void playerOffline(@PathVariable("userId") UUID userId, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
