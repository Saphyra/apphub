package com.github.saphyra.apphub.service.skyxplore.game.creation.service;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationRequest;
import com.github.saphyra.apphub.service.skyxplore.game.common.MessageSenderProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class GameCreationService {
    private final MessageSenderProxy messageSenderProxy;

    public void create(SkyXploreGameCreationRequest request) {
        WebSocketEvent event = WebSocketEvent.builder()
            .eventName(WebSocketEventName.SKYXPLORE_LOBBY_GAME_LOADED)
            .build();

        WebSocketMessage message = WebSocketMessage.builder()
            .recipients(request.getMembers().keySet())
            .event(event)
            .build();

        messageSenderProxy.sendToLobby(message);
    }
}
