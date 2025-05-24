package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import com.github.saphyra.apphub.api.skyxplore.model.game.ContainerType;
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
    private final UUID containerId;
    private final ContainerType containerType;
    private final UUID externalReference;
    private final String dataId;
    private Integer amount;

    @Override
    public String toString() {
        return String.format("ReservedStorage(%s)", new Gson().toJson(this));
    }

    public void decreaseAmount(int amount) {
        this.amount -= amount;
    }
}
