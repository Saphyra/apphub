package com.github.saphyra.apphub.service.skyxplore.lobby.service.start_game;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameLoadedService {
    private final LobbyDao lobbyDao;
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    public void gameLoaded(UUID gameId) {
        Lobby lobby = lobbyDao.findByGameId(gameId)
            .orElseThrow(() -> ExceptionFactory.reportedException(HttpStatus.NOT_FOUND, ErrorCode.LOBBY_NOT_FOUND, "Lobby not found for gameId " + gameId));
        List<UUID> recipients = lobby.getMembers()
            .values()
            .stream()
            .filter(LobbyMember::isConnected)
            .map(LobbyMember::getUserId)
            .toList();

        lobbyWebSocketHandler.sendEvent(recipients, WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED);

        lobbyDao.delete(lobby);
    }
}
