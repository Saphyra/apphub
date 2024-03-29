package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreLoadGameRequest;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.SkyXploreGameProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
class LoadGameService {
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;
    private final SkyXploreGameProxy gameProxy;

    void loadGame(Lobby lobby) {
        SkyXploreLoadGameRequest request = SkyXploreLoadGameRequest.builder()
            .host(lobby.getHost())
            .gameId(lobby.getGameId())
            .players(new ArrayList<>(lobby.getPlayers().keySet()))
            .build();

        gameProxy.loadGame(request);
        lobby.setGameCreationStarted(true);

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .build();

        lobbyWebSocketHandler.sendEvent(lobby.getPlayers().keySet(), event);
    }
}
