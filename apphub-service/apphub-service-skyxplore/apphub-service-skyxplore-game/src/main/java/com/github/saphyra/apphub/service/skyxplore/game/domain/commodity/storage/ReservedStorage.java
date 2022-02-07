package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.storage;

import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.google.gson.Gson;
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

    public void reduceAmount(int amount) {
        this.amount -= amount;
    }

    @Override
    public String toString() {
        return String.format("ReservedStorage(%s)", new Gson().toJson(this));
    }
}
