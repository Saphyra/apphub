package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AllocatedResource {
    private final UUID allocatedResourceId;
    private final UUID location; //TODO set in factory
    private final LocationType locationType; //TODO set in factory
    private final UUID externalReference;
    private final String dataId;
    private final StorageType storageType;
    private int amount;
}
