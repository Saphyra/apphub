package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Building {
    private final UUID buildingId;
    private final UUID surfaceId;
    private final String dataId;
    private volatile int level;
}
