package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageSenderControllerImplTest {
    @Mock
    private WebSocketHandler webSocketHandler;

    private MessageSenderControllerImpl underTest;

    @Mock
    private WebSocketMessage message;

    @BeforeEach
    public void setUp() {
        given(webSocketHandler.getGroup()).willReturn(MessageGroup.SKYXPLORE_LOBBY);

        underTest = new MessageSenderControllerImpl(Arrays.asList(webSocketHandler));
    }

    @Test
    public void unknownMessageGroup() {
        Throwable ex = catchThrowable(() -> underTest.sendMessage(MessageGroup.SKYXPLORE_GAME, message));

        assertThat(ex).isInstanceOf(ReportedException.class);
    }

    @Test
    public void sendMessage() {
        underTest.sendMessage(MessageGroup.SKYXPLORE_LOBBY, message);

        verify(webSocketHandler).sendEvent(message);
    }
}