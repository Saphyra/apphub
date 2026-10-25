package com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.service;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.feature.elite_base.processor.db_writer.dao.MessageDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class EliteBaseDbCacheBackfillMessageListenerTest {
    private static final byte[] MESSAGE_BODY = new byte[]{32, 3};

    @Mock
    private MessageDao messageDao;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ErrorReporterService errorReporterService;

    @Spy
    private final EdMessageLock edMessageLock = new EdMessageLock();

    @InjectMocks
    private EliteBaseDbCacheBackfillMessageListener underTest;

    @Mock
    private Message message;

    @Mock
    private EdMessage edMessage;

    @Test
    void onMessage() {
        given(message.getBody()).willReturn(MESSAGE_BODY);
        given(objectMapper.readValue(MESSAGE_BODY, EdMessage.class)).willReturn(edMessage);

        underTest.onMessage(message);

        then(messageDao).should().save(edMessage);
    }

    @Test
    void error() {
        given(message.getBody()).willReturn(MESSAGE_BODY);
        given(objectMapper.readValue(MESSAGE_BODY, EdMessage.class)).willReturn(edMessage);
        doThrow(new RuntimeException())
            .when(messageDao)
            .save(edMessage);

        underTest.onMessage(message);

        then(errorReporterService).should().report(any(), any());
    }
}