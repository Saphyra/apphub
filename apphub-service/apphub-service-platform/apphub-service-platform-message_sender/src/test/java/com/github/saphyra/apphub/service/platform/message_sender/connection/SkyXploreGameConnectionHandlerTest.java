package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.game.client.SkyXploreGameWebSocketEventApiClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreGameConnectionHandlerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String LOCALE = "locale";

    @Mock
    private SkyXploreGameWebSocketEventApiClient gameClient;

    @Mock
    private CommonConfigProperties properties;

    @InjectMocks
    private SkyXploreGameConnectionHandler underTest;

    @Mock
    private WebSocketEvent event;

    @Test
    public void getGroup() {
        assertThat(underTest.getGroup()).isEqualTo(MessageGroup.SKYXPLORE_GAME);
    }

    @Test
    public void afterConnection() {
        given(properties.getDefaultLocale()).willReturn(LOCALE);

        underTest.afterConnection(USER_ID);

        verify(gameClient).userJoinedToGame(USER_ID, LOCALE);
    }

    @Test
    public void afterDisconnection() {
        given(properties.getDefaultLocale()).willReturn(LOCALE);

        underTest.afterDisconnection(USER_ID);

        verify(gameClient).userLeftGame(USER_ID, LOCALE);
    }

    @Test
    public void handleMessage() {
        given(properties.getDefaultLocale()).willReturn(LOCALE);

        underTest.handleMessage(USER_ID, event);

        verify(gameClient).processWebSocketEvent(USER_ID, event, LOCALE);
    }

    @Test
    public void handleExpiredConnections() {
        given(properties.getDefaultLocale()).willReturn(LOCALE);

        underTest.handleExpiredConnections(Arrays.asList(USER_ID));

        verify(gameClient).userLeftGame(USER_ID, LOCALE);
    }
}