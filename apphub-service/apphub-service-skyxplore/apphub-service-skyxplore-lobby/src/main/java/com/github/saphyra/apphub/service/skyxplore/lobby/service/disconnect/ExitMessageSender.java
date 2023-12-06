package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class ExitMessageSender {
    private final CharacterProxy characterProxy;
    private final DateTimeUtil dateTimeUtil;
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    void sendExitMessage(UUID userId, Lobby lobby, boolean onlyInvited) {
        ExitMessage payload = ExitMessage.builder()
            .userId(userId)
            .host(lobby.getHost().equals(userId))
            .characterName(characterProxy.getCharacter(userId).getName())
            .expectedPlayer(lobby.getExpectedPlayers().contains(userId))
            .createdAt(dateTimeUtil.getCurrentTimeEpochMillis())
            .onlyInvited(onlyInvited)
            .build();
        lobbyWebSocketHandler.sendEvent(lobby.getPlayers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_EXIT, payload);
    }
}
