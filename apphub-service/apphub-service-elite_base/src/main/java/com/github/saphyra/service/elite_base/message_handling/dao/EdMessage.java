package com.github.saphyra.service.elite_base.message_handling.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdMessage {
    private UUID messageId;
    private LocalDateTime createdAt;
    private MessageStatus status;
    private String schemaRef;
    private String header;
    private String message;
}
