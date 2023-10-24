package com.github.saphyra.apphub.service.skyxplore.lobby.service.disconnect;

import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberResponse;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyMember;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.member.LobbyMemberToResponseConverter;
import com.github.saphyra.apphub.service.skyxplore.lobby.ws.SkyXploreLobbyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlayerDisconnectedService {
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;
    private final LobbyDao lobbyDao;
    private final SkyXploreLobbyWebSocketHandler lobbyWebSocketHandler;

    public void playerDisconnected(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        LobbyMember lobbyMember = lobby.getMembers()
            .get(userId);
        lobbyMember.setStatus(LobbyMemberStatus.DISCONNECTED);

        LobbyMemberResponse response = lobbyMemberToResponseConverter.convertMember(lobbyMember);
        lobbyWebSocketHandler.sendEvent(lobby.getMembers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_MODIFIED, response);
        lobbyWebSocketHandler.sendEvent(lobby.getMembers().keySet(), WebSocketEventName.SKYXPLORE_LOBBY_PLAYER_DISCONNECTED, response);
    }
}
