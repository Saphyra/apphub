package com.github.saphyra.apphub.service.skyxplore.lobby.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.active_friend.ActiveFriendsService;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.WebSocketEventHandlerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreLobbyWsControllerImplTest {
    private static final UUID FROM = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private ActiveFriendsService activeFriendsService;

    @Mock
    private WebSocketEventHandlerService webSocketEventHandlerService;

    @InjectMocks
    private SkyXploreLobbyWsControllerImpl underTest;

    @Mock
    private WebSocketEvent event;

    @Test
    public void processWebSocketEvent() {
        underTest.processWebSocketEvent(FROM, event);

        verify(webSocketEventHandlerService).handle(FROM, event);
    }

    @Test
    public void characterOnline() {
        underTest.playerOnline(USER_ID);

        verify(activeFriendsService).playerOnline(USER_ID);
    }

    @Test
    public void playerOffline() {
        underTest.playerOffline(USER_ID);

        verify(activeFriendsService).playerOffline(USER_ID);
    }
}