package com.github.saphyra.service.elite_base.message_handling.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
class MessageConverter extends ConverterBase<MessageEntity, EdMessage> {
    private final UuidConverter uuidConverter;

    @Override
    protected MessageEntity processDomainConversion(EdMessage domain) {
        return MessageEntity.builder()
            .messageId(uuidConverter.convertDomain(domain.getMessageId()))
            .createdAt(domain.getCreatedAt().toString())
            .status(domain.getStatus())
            .schemaRef(domain.getSchemaRef())
            .header(domain.getHeader())
            .message(domain.getMessage())
            .build();
    }

    @Override
    protected EdMessage processEntityConversion(MessageEntity entity) {
        return EdMessage.builder()
            .messageId(uuidConverter.convertEntity(entity.getMessageId()))
            .createdAt(LocalDateTime.parse(entity.getCreatedAt()))
            .status(entity.getStatus())
            .schemaRef(entity.getSchemaRef())
            .header(entity.getHeader())
            .message(entity.getMessage())
            .build();
    }
}
