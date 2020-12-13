package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Alliance;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Invitation;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.CharacterProxy;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class JoinToLobbyService {
    private final LobbyDao lobbyDao;
    private final CharacterProxy characterProxy;
    private final MessageSenderProxy messageSenderProxy;

    public void acceptInvitation(UUID userId, UUID invitorId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(invitorId);

        List<Invitation> invitations = lobby.getInvitations();
        Invitation invitation = invitations
            .stream()
            .filter(i -> i.getCharacterId().equals(userId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("You are not invited to this lobby.")); //TODO proper exception

        invitations.remove(invitation);

        Member member = Member.builder()
            .userId(userId)
            .build();
        lobby.getMembers().put(userId, member);
    }

    public void userJoinedToLobby(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);
        Member member = lobby.getMembers().get(userId);
        member.setConnected(true);

        JoinMessage joinMessage = JoinMessage.builder()
            .characterName(characterProxy.getCharacter(userId).getName())
            .userId(userId)
            .host(userId.equals(lobby.getHost()))
            .alliances(lobby.getAlliances().stream().map(Alliance::getAllianceName).collect(Collectors.toList()))
            .build();
        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(new ArrayList<>(lobby.getMembers().keySet()))
            .event(WebSocketEvent.builder()
                .eventName(WebSocketEventName.SKYXPLORE_LOBBY_JOIN_TO_LOBBY)
                .payload(joinMessage)
                .build())
            .build();
        List<UUID> disconnectedUsers = messageSenderProxy.sendToLobby(message); //TODO handle disconnectedUsers
    }

    @Data
    @AllArgsConstructor
    @Builder
    private static class JoinMessage {
        private String characterName;
        private UUID userId;
        private boolean host;
        private List<String> alliances;
    }
}
