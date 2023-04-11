package com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.skyxplore.response.lobby.LobbyMemberStatus;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
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
    private final MessageSenderProxy messageSenderProxy;
    private final LobbyMemberToResponseConverter lobbyMemberToResponseConverter;

    @Override
    public boolean canHandle(WebSocketEventName eventName) {
        return WebSocketEventName.SKYXPLORE_LOBBY_SET_READINESS == eventName;
    }

    @Override
    public void handle(UUID from, WebSocketEvent event) {
        boolean readiness = Boolean.parseBoolean(event.getPayload().toString());
        log.info("Setting {}'s readiness to {}", from, readiness);
        Lobby lobby = lobbyDao.findByUserIdValidated(from);

        Member member = lobby.getMembers().get(from);
        member.setStatus(readiness ? LobbyMemberStatus.READY : LobbyMemberStatus.NOT_READY);

        messageSenderProxy.lobbyMemberModified(lobbyMemberToResponseConverter.convertMember(member), lobby.getMembers().keySet());
    }
}
