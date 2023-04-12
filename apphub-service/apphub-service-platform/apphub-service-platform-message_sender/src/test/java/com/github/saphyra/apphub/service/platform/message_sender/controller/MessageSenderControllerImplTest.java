package com.github.saphyra.apphub.service.platform.message_sender.controller;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketEvent;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.lib.exception.ReportedException;
import com.github.saphyra.apphub.service.platform.message_sender.connection.WebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MessageSenderControllerImplTest {
    @Mock
    private WebSocketHandler webSocketHandler;

    @Mock
    private ErrorReporterService errorReporterService;

    private MessageSenderControllerImpl underTest;

    @Mock
    private WebSocketMessage message;

    @Mock
    private WebSocketMessage errorsMessage;

    @BeforeEach
    public void setUp() {
        given(webSocketHandler.getGroup()).willReturn(MessageGroup.SKYXPLORE_LOBBY);

        underTest = new MessageSenderControllerImpl(Arrays.asList(webSocketHandler), errorReporterService);
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

    @Test
    void sendMessageBulk() {
        Throwable exception = new RuntimeException();
        doThrow(exception).when(webSocketHandler).sendEvent(errorsMessage);

        given(errorsMessage.getEvent()).willReturn(new WebSocketEvent());

        underTest.sendMessage(MessageGroup.SKYXPLORE_LOBBY, List.of(errorsMessage, message));

        verify(webSocketHandler).sendEvent(errorsMessage);
        verify(webSocketHandler).sendEvent(message);
        verify(errorReporterService).report(anyString(), eq(exception));
    }
}