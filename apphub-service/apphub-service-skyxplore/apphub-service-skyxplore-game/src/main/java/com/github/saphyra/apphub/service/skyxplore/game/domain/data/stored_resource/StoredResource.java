package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class StoredResource {
    @NonNull
    private final UUID storedResourceId;

    @NonNull
    private final UUID location;

    @NonNull
    private final String dataId;

    @NonNull
    private Integer amount;

    public void decreaseAmount(int amount) {
        this.amount -= amount;
    }

    public void increaseAmount(int amount) {
        this.amount += amount;
    }
}
