package com.github.saphyra.apphub.service.elite_base.message_processing.dao.last_update;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class LastUpdate {
    private final UUID externalReference;
    private final EntityType type;
    private LocalDateTime lastUpdate;
}
