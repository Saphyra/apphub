package com.github.saphyra.apphub.service.skyxplore.lobby.service.event;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEventName;
import com.github.saphyra.apphub.service.skyxplore.lobby.service.event.handler.WebSocketEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WebSocketEventHandlerServiceTest {
    private static final UUID FROM = UUID.randomUUID();

    @Mock
    private WebSocketEventHandler eventHandler1;

    @Mock
    private WebSocketEventHandler eventHandler2;

    private WebSocketEventHandlerService underTest;

    @Mock
    private WebSocketEvent event;

    @BeforeEach
    public void setUp() {
        underTest = new WebSocketEventHandlerService(Arrays.asList(eventHandler1, eventHandler2));
    }

    @Test
    public void handle() {
        given(event.getEventName()).willReturn(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT);
        given(eventHandler1.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).willReturn(true);
        given(eventHandler2.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).willReturn(false);

        underTest.handle(FROM, event);

        verify(eventHandler1).handle(FROM, event);
        verify(eventHandler2, times(0)).handle(any(), any());
    }
}