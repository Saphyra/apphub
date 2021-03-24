package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.service.skyxplore.game.common.CustomLocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageSenderProxyTest {
    private static final String LOCALE = "locale";

    @Mock
    private CustomLocaleProvider localeProvider;

    @Mock
    private MessageSenderApiClient messageSenderClient;

    @InjectMocks
    private MessageSenderProxy underTest;

    @Mock
    private WebSocketMessage message;

    @Before
    public void setUp() {
        given(localeProvider.getLocale()).willReturn(LOCALE);
    }

    @Test
    public void sendToLobby() {
        underTest.sendToLobby(message);

        verify(messageSenderClient).sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, LOCALE);
    }

    @Test
    public void sendToGame() {
        underTest.sendToGame(message);

        verify(messageSenderClient).sendMessage(MessageGroup.SKYXPLORE_GAME, message, LOCALE);
    }
}