package com.github.saphyra.apphub.service.skyxplore.game.domain.data.stored_resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
//TODO unit test
public class StoredResource {
    private final UUID storedResourceId;
    private final UUID location;
    private final String dataId;
    private int amount;

    public void decreaseAmount(int amount) {
        this.amount -= amount;
    }

    public void increaseAmount(int amount) {
        this.amount += amount;
    }
}
