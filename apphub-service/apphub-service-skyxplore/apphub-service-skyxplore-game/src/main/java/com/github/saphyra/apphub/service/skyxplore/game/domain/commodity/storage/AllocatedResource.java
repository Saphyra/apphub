package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

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
    private final UUID location;
    private final LocationType locationType;
    private final UUID externalReference;
    private final String dataId;
    private int amount;

    public AllocatedResource increaseAmount(int amount) {
        this.amount += amount;
        return this;
    }
}
