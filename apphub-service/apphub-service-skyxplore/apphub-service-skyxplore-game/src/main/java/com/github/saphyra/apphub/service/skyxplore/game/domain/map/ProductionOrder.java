package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
//TODO save to database
public class ProductionOrder {
    private final UUID productionOrderId;
    private final UUID location;
    private final LocationType locationType;
    private UUID assignee;
    private final UUID externalReference;
    private final String dataId;
    private int amount;
    private int requiredWorkPoints;
    @Builder.Default
    private int currentWorkPoints = 0;
}
