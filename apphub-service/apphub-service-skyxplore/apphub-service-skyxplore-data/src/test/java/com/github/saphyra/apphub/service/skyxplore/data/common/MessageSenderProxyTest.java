package com.github.saphyra.apphub.service.skyxplore.data.common;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
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
    private CustomLocaleProvider customLocaleProvider;

    @InjectMocks
    private MessageSenderProxy underTest;

    @Mock
    private WebSocketMessage message;

    @Test
    public void sendToMainMenu() {
        given(customLocaleProvider.getLocale()).willReturn(LOCALE);

        underTest.sendToMainMenu(message);

        verify(messageSenderApiClient).sendMessage(MessageGroup.SKYXPLORE_MAIN_MENU, message, LOCALE);
    }
}