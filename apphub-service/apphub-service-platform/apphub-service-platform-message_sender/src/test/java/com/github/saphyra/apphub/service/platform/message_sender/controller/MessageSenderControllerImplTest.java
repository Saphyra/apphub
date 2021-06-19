package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MessageSenderControllerImplTest {
    @Mock
    private WebSocketHandler webSocketHandler;

    private MessageSenderControllerImpl underTest;

    @Mock
    private WebSocketMessage message;

    @Before
    public void setUp() {
        given(webSocketHandler.getGroup()).willReturn(MessageGroup.SKYXPLORE_LOBBY);

        underTest = new MessageSenderControllerImpl(Arrays.asList(webSocketHandler));
    }

    @Test(expected = ReportedException.class)
    public void unknownMessageGroup() {
        underTest.sendMessage(MessageGroup.SKYXPLORE_GAME, message);
    }

    @Test
    public void sendMessage() {
        underTest.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message);

        verify(webSocketHandler).sendEvent(message);
    }
}