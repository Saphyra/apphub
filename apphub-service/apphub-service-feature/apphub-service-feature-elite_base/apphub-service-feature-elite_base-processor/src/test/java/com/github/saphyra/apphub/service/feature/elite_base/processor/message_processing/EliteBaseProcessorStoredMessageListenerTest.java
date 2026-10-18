package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor.EdMessageProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EliteBaseProcessorStoredMessageListenerTest {
    private static final byte[] BODY = new byte[]{34, 23};

    @Mock
    private EdMessageProcessor edMessageProcessor;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EliteBaseProcessorStoredMessageListener underTest;

    @Mock
    private Message message;

    @Mock
    private EdMessage edMessage;

    @Test
    void onMessage() {
        given(message.getBody()).willReturn(BODY);
        given(objectMapper.readValue(BODY, EdMessage.class)).willReturn(edMessage);

        underTest.onMessage(message);

        then(edMessageProcessor).should().processMessage(edMessage);
    }
}