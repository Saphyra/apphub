package com.github.saphyra.apphub.service.feature.elite_base.processor.dao.last_update;

import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.ObjectType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class LastUpdate {
    private final String externalReference;
    private final ObjectType type;
    private LocalDateTime lastUpdate;
}
