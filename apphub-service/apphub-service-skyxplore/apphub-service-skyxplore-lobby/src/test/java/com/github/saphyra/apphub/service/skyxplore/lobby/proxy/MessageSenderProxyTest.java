package com.github.saphyra.apphub.service.skyxplore.lobby.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
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
    private MessageSenderApiClient messageSenderApiClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private MessageSenderProxy underTest;

    @Mock
    private WebSocketMessage message;

    @Before
    public void setUp() {
        given(localeProvider.getLocaleValidated()).willReturn(LOCALE);
    }

    @Test
    public void sendToMainMenu() {
        underTest.sendToMainMenu(message);

        verify(messageSenderApiClient).sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, message, LOCALE);
    }

    @Test
    public void sendToLobby() {
        underTest.sendToLobby(message);

        verify(messageSenderApiClient).sendMessage(MessageGroup.SKYXPLORE_LOBBY, message, LOCALE);
    }
}