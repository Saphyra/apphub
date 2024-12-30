package com.github.saphyra.apphub.service.elite_base.dao.minor_faction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class MinorFaction {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private String factionName;
    private FactionState state;
}
