package com.github.saphyra.apphub.service.skyxplore.game.domain.data.reserved_storage;

import com.github.saphyra.apphub.api.feature.skyxplore.model.game.ContainerType;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ReservedStorage {
    @NonNull
    private final UUID reservedStorageId;
    @NonNull
    private final UUID containerId;
    @NonNull
    private final ContainerType containerType;
    @NonNull
    private final UUID externalReference;
    @NonNull
    private final String dataId;
    @NonNull
    private Integer amount;

    @Builder.Default
    private boolean existing = false;

    @Override
    public String toString() {
        return String.format("ReservedStorage(%s)", new Gson().toJson(this));
    }

    public void decreaseAmount(int amount) {
        this.amount -= amount;
    }
}
