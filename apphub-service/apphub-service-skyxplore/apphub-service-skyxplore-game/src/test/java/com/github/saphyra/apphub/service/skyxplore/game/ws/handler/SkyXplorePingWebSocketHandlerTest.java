package com.github.saphyra.apphub.service.skyxplore.game.ws.handler;

import com.github.saphyra.apphub.lib.common_domain.WebSocketEventName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SkyXplorePingWebSocketHandlerTest {
    @InjectMocks
    private SkyXplorePingWebSocketHandler underTest;

    @Test
    public void canHandle_ping() {
        assertThat(underTest.canHandle(WebSocketEventName.PING)).isTrue();
    }

    @Test
    public void canHandle_other() {
        assertThat(underTest.canHandle(WebSocketEventName.SKYXPLORE_GAME_USER_LEFT)).isFalse();
    }
}