package com.github.saphyra.apphub.service.elite_base.message_handling.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
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
class MessageConverterTest {
    private static final UUID MESSAGE_ID = UUID.randomUUID();
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final String SCHEMA_REF = "schema-ref";
    private static final String HEADER = "header";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_ID_STRING = "message-id";
    private static final UUID EXCEPTION_ID = UUID.randomUUID();
    private static final String EXCEPTION_ID_STRING = "exception-id";

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private MessageConverter underTest;

    @Test
    void convertDomain() {
        EdMessage domain = EdMessage.builder()
            .messageId(MESSAGE_ID)
            .createdAt(CREATED_AT)
            .status(MessageStatus.ARRIVED)
            .schemaRef(SCHEMA_REF)
            .header(HEADER)
            .message(MESSAGE)
            .exceptionId(EXCEPTION_ID)
            .build();

        given(uuidConverter.convertDomain(MESSAGE_ID)).willReturn(MESSAGE_ID_STRING);
        given(uuidConverter.convertDomain(EXCEPTION_ID)).willReturn(EXCEPTION_ID_STRING);

        assertThat(underTest.convertDomain(domain))
            .returns(MESSAGE_ID_STRING, MessageEntity::getMessageId)
            .returns(CREATED_AT.toString(), MessageEntity::getCreatedAt)
            .returns(MessageStatus.ARRIVED, MessageEntity::getStatus)
            .returns(SCHEMA_REF, MessageEntity::getSchemaRef)
            .returns(HEADER, MessageEntity::getHeader)
            .returns(MESSAGE, MessageEntity::getMessage)
            .returns(EXCEPTION_ID_STRING, MessageEntity::getExceptionId);
    }

    @Test
    void convertEntity() {
        MessageEntity entity = MessageEntity.builder()
            .messageId(MESSAGE_ID_STRING)
            .createdAt(CREATED_AT.toString())
            .status(MessageStatus.ARRIVED)
            .schemaRef(SCHEMA_REF)
            .header(HEADER)
            .message(MESSAGE)
            .exceptionId(EXCEPTION_ID_STRING)
            .build();

        given(uuidConverter.convertEntity(MESSAGE_ID_STRING)).willReturn(MESSAGE_ID);
        given(uuidConverter.convertEntity(EXCEPTION_ID_STRING)).willReturn(EXCEPTION_ID);

        assertThat(underTest.convertEntity(entity))
            .returns(MESSAGE_ID, EdMessage::getMessageId)
            .returns(CREATED_AT, EdMessage::getCreatedAt)
            .returns(MessageStatus.ARRIVED, EdMessage::getStatus)
            .returns(SCHEMA_REF, EdMessage::getSchemaRef)
            .returns(HEADER, EdMessage::getHeader)
            .returns(MESSAGE, EdMessage::getMessage)
            .returns(EXCEPTION_ID, EdMessage::getExceptionId);
    }
}