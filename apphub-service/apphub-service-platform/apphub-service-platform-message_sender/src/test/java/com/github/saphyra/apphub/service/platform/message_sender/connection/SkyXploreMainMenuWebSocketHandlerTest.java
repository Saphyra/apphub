package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.skyxplore.lobby.client.SkyXploreLobbyWsApiClient;
import com.github.saphyra.apphub.lib.common_util.CommonConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkyXploreMainMenuWebSocketHandlerTest {
    private static final String LOCALE = "locale";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private SkyXploreLobbyWsApiClient lobbyWsClient;

    @Mock
    private CommonConfigProperties commonConfigProperties;

    @InjectMocks
    private SkyXploreMainMenuWebSocketHandler underTest;

    @Test
    public void getGroup() {
        assertThat(underTest.getGroup()).isEqualTo(MessageGroup.SKYXPLORE_MAIN_MENU);
    }

    @Test
    public void afterConnection() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.afterConnection(USER_ID);

        verify(lobbyWsClient).playerOnline(USER_ID, LOCALE);
    }

    @Test
    public void afterDisconnection() {
        given(commonConfigProperties.getDefaultLocale()).willReturn(LOCALE);

        underTest.afterDisconnection(USER_ID);

        verify(lobbyWsClient).playerOffline(USER_ID, LOCALE);
    }
}