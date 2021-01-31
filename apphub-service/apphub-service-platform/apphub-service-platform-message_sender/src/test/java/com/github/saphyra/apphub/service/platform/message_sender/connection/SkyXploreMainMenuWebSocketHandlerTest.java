package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyWsApiClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SkyXploreMainMenuWebSocketHandlerTest {
    private static final String LOCALE = "locale";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SkyXploreLobbyWsApiClient lobbyWsClient;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private SkyXploreMainMenuWebSocketHandler underTest;

    @Before
    public void setUp() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);
    }

    @Test
    public void getGroup() {
        assertThat(underTest.getGroup()).isEqualTo(MessageGroup.SKYXPLORE_MAIN_MENU);
    }

    @Test
    public void afterConnection() {
        underTest.afterConnection(USER_ID);

        verify(lobbyWsClient).characterOnline(USER_ID, LOCALE);
    }

    @Test
    public void afterDisconnection() {
        underTest.afterDisconnection(USER_ID);

        verify(lobbyWsClient).characterOffline(USER_ID, LOCALE);
    }
}