package com.github.saphyra.apphub.api.skyxplore.lobby.client;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_util.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient("skyxplore-lobby")
public interface SkyXploreLobbyApiClient {
    @PutMapping(Endpoints.SKYXPLORE_CREATE_LOBBY)
    void createLobbyIfNotExists(@RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.SKYXPLORE_JOIN_LOBBY)
    void joinLobby(@PathVariable("invitorId") UUID invitorId, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) String accessTokenHeader, @RequestHeader(Constants.LOCALE_HEADER) String locale);

    @PostMapping(Endpoints.INTERNAL_SKYXPLORE_LOBBY_PROCESS_WEB_SOCKET_EVENTS)
    void processWebSocketEvent(@PathVariable("userId") UUID from, @RequestBody WebSocketEvent event, @RequestHeader(Constants.LOCALE_HEADER) String locale);
}
