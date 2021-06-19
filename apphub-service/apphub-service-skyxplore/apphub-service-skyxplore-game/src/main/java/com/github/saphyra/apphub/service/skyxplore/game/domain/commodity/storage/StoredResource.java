package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import java.util.UUID;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StoredResource {
    private final UUID storedResourceId;
    private final UUID location;
    private final LocationType locationType;
    private final String dataId;
    private int amount;
}
