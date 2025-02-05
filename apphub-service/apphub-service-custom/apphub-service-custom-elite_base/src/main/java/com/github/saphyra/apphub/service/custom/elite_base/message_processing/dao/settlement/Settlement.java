package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.settlement;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class Settlement {
    private UUID id;
    private LocalDateTime lastUpdate;
    private UUID starSystemId;
    private UUID bodyId;
    private String settlementName;
    private Long marketId;
    private Double longitude;
    private Double latitude;
}
