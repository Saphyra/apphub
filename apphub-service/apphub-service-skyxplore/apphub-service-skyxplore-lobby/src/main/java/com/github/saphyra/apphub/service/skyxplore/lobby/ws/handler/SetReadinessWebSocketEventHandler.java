package com.github.saphyra.apphub.service.skyxplore.lobby.ws.handler;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEvent;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SetReadinessWebSocketEventHandler implements WebSocketEventHandler {
    private final LobbyDao lobbyDao;
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event, SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler) {
        boolean readiness = Boolean.parseBoolean(event.getPayload().toString());
        log.info("Setting {}'s readiness to {}", from, readiness);
        Lobby lobby = lobbyDao.findByUserIdValidated(from);

        LobbyMember lobbyMember = lobby.getMembers()
            .get(from);
        lobbyMember.setStatus(readiness ? LobbyMemberStatus.READY : LobbyMemberStatus.NOT_READY);

        LobbyMemberResponse lobbyMemberResponse = lobbyMemberToResponseConverter.convertMember(lobbyMember);

        lobbyWebSocketHandler.sendEvent(lobby.getMembers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS, lobbyMemberResponse);
    }
}
