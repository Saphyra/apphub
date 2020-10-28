package com.github.saphyra.apphub.service.platform.event_gateway.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "event_processor", schema = "event_gateway")
@Entity
class EventProcessorEntity {
    @Id
    private String eventProcessorId;
    private String serviceName;
    private String url;
    private String eventName;
    private LocalDateTime lastAccess;
}
