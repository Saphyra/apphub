package com.github.saphyra.apphub.service.elite_base.message_handling.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "elite_base", name = "message")
class MessageEntity {
    @Id
    private String messageId;
    private String createdAt;
    @Enumerated(EnumType.STRING)
    private MessageStatus status;
    private String schemaRef;
    private String header;
    private String message;
    private String exceptionId;
}
