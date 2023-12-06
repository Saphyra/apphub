package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.creation.GameSettingsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class AiService {
    private final IdGenerator idGenerator;
    private final LobbyDao lobbyDao;
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;
    private final GameSettingsProperties gameSettingsProperties;

    void removeAi(UUID userId, UUID aiUserId) {
        Lobby lobby = lobbyDao.findByHostValidated(userId);

        lobby.getAis()
            .removeIf(aiPlayer -> aiPlayer.getUserId().equals(aiUserId));

        lobbyWebSocketHandler.sendEvent(lobby.getPlayers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_AI_REMOVED, aiUserId);
    }

    public void createOrModifyAi(UUID userId, AiPlayer aiPlayer) {
        if (isNull(aiPlayer.getName())) {
            throw ExceptionFactory.invalidParam("name", "must not be null");
        }

        if (aiPlayer.getName().length() < 3) {
            throw ExceptionFactory.invalidParam("name", "too short");
        }

        if (aiPlayer.getName().length() > 30) {
            throw ExceptionFactory.invalidParam("name", "too long");
        }

        Lobby lobby = lobbyDao.findByHostValidated(userId);
        if (!isNull(aiPlayer.getAllianceId()) && !lobby.getAlliances().stream().map(Alliance::getAllianceId).toList().contains(aiPlayer.getAllianceId())) {
            throw ExceptionFactory.invalidParam("allianceId", "does not exist");
        }

        if (!isNull(aiPlayer.getUserId())) {
            lobby.getAis()
                .removeIf(ai -> ai.getUserId().equals(aiPlayer.getUserId()));
        } else {
            aiPlayer.setUserId(idGenerator.randomUuid());
        }

        if (lobby.getAis().size() >= gameSettingsProperties.getMaxAiCount()) {
            throw ExceptionFactory.notLoggedException(HttpStatus.BAD_REQUEST, ErrorCode.TOO_MANY_AIS);
        }

        lobby.getAis()
            .add(aiPlayer);

        lobbyWebSocketHandler.sendEvent(lobby.getPlayers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED, aiPlayer);
    }
}
