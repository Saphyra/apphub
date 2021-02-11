package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class StorageSetting {
    private final UUID storageSettingsId;
    private final UUID location; //TODO set in factory
    private final LocationType locationType; //TODO set in factory
    private final String dataId;
    private int targetAmount;
    private int priority;
    private int batchSize;
}
