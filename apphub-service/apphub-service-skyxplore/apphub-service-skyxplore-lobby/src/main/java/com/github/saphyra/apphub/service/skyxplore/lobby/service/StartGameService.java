package com.github.saphyra.apphub.service.skyxplore.lobby.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Lobby;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.LobbyDao;
import com.github.saphyra.apphub.service.skyxplore.lobby.dao.Member;
import com.github.saphyra.apphub.service.skyxplore.lobby.proxy.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StartGameService {
    private final LobbyDao lobbyDao;
    private final MessageSenderProxy messageSenderProxy;

    public void startGame(UUID userId) {
        Lobby lobby = lobbyDao.findByUserIdValidated(userId);

        if (!lobby.getHost().equals(userId)) {
            throw new RuntimeException(userId + " must not start the game."); //TODO proper exception
        }

        boolean allReady = lobby.getMembers()
            .values()
            .stream()
            .allMatch(Member::isReady);
        if (!allReady) {
            throw new RuntimeException("There are member(s) not ready.");
        }

        lobby.setGameCreationStarted(true);
        //TODO trigger game start

        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_CREATION_INITIATED)
            .build();

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(lobby.getMembers().keySet())
            .event(event)
            .build();
        messageSenderProxy.sendToLobby(message);
    }
}
