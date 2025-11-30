package com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageFactory {
    private final IdGenerator idGenerator;
    private final DateTimeUtil dateTimeUtil;
    private final ObjectMapper objectMapper;

    public EdMessage create(String content) {
        ParsedMessage parsedMessage = objectMapper.readValue(content, ParsedMessage.class);
        log.debug("Message arrived: {}", parsedMessage.getSchemaRef());

        return EdMessage.builder()
            .messageId(idGenerator.randomUuid())
            .createdAt(dateTimeUtil.getCurrentDateTime())
            .status(MessageStatus.ARRIVED)
            .schemaRef(parsedMessage.getSchemaRef())
            .header(objectMapper.writeValueAsString(parsedMessage.getHeader()))
            .message(objectMapper.writeValueAsString(parsedMessage.getMessage()))
            .build();
    }

    @NoArgsConstructor
    @Data
    static class ParsedMessage {
        @JsonProperty("$schemaRef")
        private String schemaRef;
        private Object header;
        private Object message;
    }
}
