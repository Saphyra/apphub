package com.github.saphyra.apphub.service.skyxplore.game.proxy;

import com.github.saphyra.apphub.api.platform.message_sender.client.MessageSenderApiClient;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.web_utils.CustomLocaleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageSenderProxyTest {
    private static final String LOCALE = "locale";

    @Mock
    private CustomLocaleProvider localeProvider;

    @Mock
    private MessageSenderApiClient messageSenderClient;

    @Mock
    private ExecutorServiceBean executorServiceBean;

    @InjectMocks
    private MessageSenderProxy underTest;

    @Mock
    private WebSocketMessage message;

    @BeforeEach
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

        ArgumentCaptor<Runnable> argumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(executorServiceBean).execute(argumentCaptor.capture());
        argumentCaptor.getValue()
            .run();

        verify(messageSenderClient).sendMessage(MessageGroup.SKYXPLORE_GAME, message, LOCALE);
    }
}