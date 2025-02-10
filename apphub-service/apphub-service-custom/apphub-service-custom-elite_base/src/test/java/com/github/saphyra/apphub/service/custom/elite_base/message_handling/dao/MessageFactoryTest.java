package com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao;

import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageFactoryTest {
    private static final String CONTENT = "content";
    private static final UUID MESSAGE_ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String SCHEMA_REF = "schema-ref";
    private static final Object HEADER = "header";
    private static final Object MESSAGE = "message";
    private static final String HEADER_STRING = "header-string";
    private static final String MESSAGE_STRING = "message-string";

    @Mock
    private IdGenerator idGenerator;

    @Mock
    private DateTimeUtil dateTimeUtil;

    @Mock
    private ObjectMapperWrapper objectMapperWrapper;

    @InjectMocks
    private MessageFactory underTest;

    @Mock
    private MessageFactory.ParsedMessage parsedMessage;

    @Test
    void create() {
        given(idGenerator.randomUuid()).willReturn(MESSAGE_ID);
        given(dateTimeUtil.getCurrentDateTime()).willReturn(CREATED_AT);
        given(objectMapperWrapper.readValue(CONTENT, MessageFactory.ParsedMessage.class)).willReturn(parsedMessage);
        given(parsedMessage.getSchemaRef()).willReturn(SCHEMA_REF);
        given(parsedMessage.getHeader()).willReturn(HEADER);
        given(parsedMessage.getMessage()).willReturn(MESSAGE);
        given(objectMapperWrapper.writeValueAsString(HEADER)).willReturn(HEADER_STRING);
        given(objectMapperWrapper.writeValueAsString(MESSAGE)).willReturn(MESSAGE_STRING);

        assertThat(underTest.create(CONTENT))
            .returns(MESSAGE_ID, EdMessage::getMessageId)
            .returns(CREATED_AT, EdMessage::getCreatedAt)
            .returns(MessageStatus.ARRIVED, EdMessage::getStatus)
            .returns(SCHEMA_REF, EdMessage::getSchemaRef)
            .returns(HEADER_STRING, EdMessage::getHeader)
            .returns(MESSAGE_STRING, EdMessage::getMessage);
    }
}