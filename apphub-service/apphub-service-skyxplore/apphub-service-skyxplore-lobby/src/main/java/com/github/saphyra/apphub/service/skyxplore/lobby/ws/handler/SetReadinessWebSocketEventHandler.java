package com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyPlayerStatus;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyPlayer;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.player.LobbyPlayerToResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SetReadinessWebSocketEventHandler implements WebSocketEventHandler {
    private final LobbyDao lobbyDao;
    private final LobbyPlayerToResponseConverter lobbyPlayerToResponseConverter;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event, SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler) {
        boolean readiness = Boolean.parseBoolean(event.getPayload().toString());
        log.info("Setting {}'s readiness to {}", from, readiness);
        Lobby lobby = lobbyDao.findByUserIdValidated(from);

        LobbyPlayer lobbyPlayer = lobby.getPlayers()
            .get(from);
        lobbyPlayer.setStatus(readiness ? LobbyPlayerStatus.READY : LobbyPlayerStatus.NOT_READY);

        LobbyPlayerResponse lobbyPlayerResponse = lobbyPlayerToResponseConverter.convertPlayer(lobbyPlayer);

        lobbyWebSocketHandler.sendEvent(lobby.getPlayers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, lobbyPlayerResponse);
    }
}
