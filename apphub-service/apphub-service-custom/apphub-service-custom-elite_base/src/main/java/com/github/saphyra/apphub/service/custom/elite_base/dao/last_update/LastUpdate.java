package com.github.saphyra.apphub.service.custom.elite_base.dao.last_update;

import com.github.saphyra.apphub.service.custom.elite_base.dao.item.ItemType;
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
    private final ItemType type;
    private LocalDateTime lastUpdate;
}
