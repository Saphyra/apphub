package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyApiClient;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyWsApiClient;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import com.github.saphyra.apphub.lib.common_util.converter.AccessTokenHeaderConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreLobbyWebSocketHandlerTest {
    private static final String LOCALE = "locale";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ACCESS_TOKEN_STRING = "access-token";

    @Mock
    private AccessTokenHeaderConverter accessTokenHeaderConverter;

    @Mock
    private SkyXploreLobbyApiClient lobbyClient;

    @Mock
    private SkyXploreLobbyWsApiClient lobbyWsClient;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private SkyXploreLobbyWebSocketHandler underTest;

    @Mock
    private WebSocketEvent event;

    @Test
    public void getGroup() {
        assertThat(underTest.getGroup()).isEqualTo(MessageGroup.SKYXPLORE_LOBBY);
    }

    @Test
    public void afterConnection() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.afterConnection(USER_ID);

        verify(lobbyClient).userJoinedToLobby(USER_ID, LOCALE);
    }

    @Test
    public void afterDisconnection() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.afterDisconnection(USER_ID);

        verify(lobbyClient).userLeftLobby(USER_ID, LOCALE);
    }

    @Test
    public void handleMessage() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.handleMessage(USER_ID, event);

        verify(lobbyWsClient).processWebSocketEvent(USER_ID, event, LOCALE);
    }

    @Test
    public void handleExpiredConnections() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
        given(accessTokenHeaderConverter.convertDomain(any(AccessTokenHeader.class))).willReturn(ACCESS_TOKEN_STRING);

        underTest.handleExpiredConnections(Arrays.asList(USER_ID));

        ArgumentCaptor<AccessTokenHeader> argumentCaptor = ArgumentCaptor.forClass(AccessTokenHeader.class);
        verify(accessTokenHeaderConverter).convertDomain(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(argumentCaptor.getValue().getRoles()).contains("SKYXPLORE");

        verify(lobbyClient).exitFromLobby(ACCESS_TOKEN_STRING, LOCALE);
    }
}