package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.AiPlayer;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
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
    private final MessageSenderProxy messageSenderProxy;
    private final GameSettingsProperties gameSettingsProperties;

    void removeAi(UUID userId, UUID aiUserId) {
        Lobby lobby = lobbyDao.findByHostValidated(userId);

        lobby.getAis()
            .removeIf(aiPlayer -> aiPlayer.getUserId().equals(aiUserId));

        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(
            WebSocketEventName.SKYXPLORE_LOBBY_AI_REMOVED,
            lobby.getMembers().keySet(),
            aiUserId
        );

        messageSenderProxy.sendToLobby(message);
    }

    public void createOrModifyAi(UUID userId, AiPlayer aiPlayer) {
        Lobby lobby = lobbyDao.findByHostValidated(userId);

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

        WebSocketMessage message = WebSocketMessage.forEventAndRecipients(
            WebSocketEventName.SKYXPLORE_LOBBY_AI_MODIFIED,
            lobby.getMembers().keySet(),
            aiPlayer
        );

        messageSenderProxy.sendToLobby(message);
    }
}
