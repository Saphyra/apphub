package com.github.saphyra.apphub.service.admin_panel.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.web_utils.LocaleProvider;
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
    private MessageSenderApiClient messageSenderClient;

    @Mock
    private LocaleProvider localeProvider;

    @InjectMocks
    private MessageSenderProxy underTest;

    @Mock
    private WebSocketMessage message;

    @Test
    public void sendToMonitoring() {
        given(localeProvider.getOrDefault()).willReturn(LOCALE);

        underTest.sendToMonitoring(message);

        verify(messageSenderClient).sendMessage(MessageGroup.ADMIN_PANEL_MONITORING, message, LOCALE);
    }
}