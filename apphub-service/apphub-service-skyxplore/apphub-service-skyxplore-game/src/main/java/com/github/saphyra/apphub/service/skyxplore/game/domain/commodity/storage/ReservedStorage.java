package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ReservedStorage {
    private final UUID reservedStorageId;
    private final UUID location;
    private final LocationType locationType;
    private final UUID externalReference;
    private final String dataId;
    private int amount;

    public ReservedStorage reduceAmount(int amount) {
        this.amount -= amount;
        return this;
    }
}
