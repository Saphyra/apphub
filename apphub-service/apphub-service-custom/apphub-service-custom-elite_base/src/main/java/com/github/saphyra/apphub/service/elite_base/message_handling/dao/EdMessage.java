package com.github.saphyra.apphub.service.elite_base.message_handling.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"messageId", "createdAt", "status", "schemaRef", "exceptionId", "retryCount"})
public class EdMessage {
    private UUID messageId;
    private LocalDateTime createdAt;
    private MessageStatus status;
    private String schemaRef;
    private String header;
    private String message;
    private UUID exceptionId;
    @Builder.Default
    private Integer retryCount = 0;
}
