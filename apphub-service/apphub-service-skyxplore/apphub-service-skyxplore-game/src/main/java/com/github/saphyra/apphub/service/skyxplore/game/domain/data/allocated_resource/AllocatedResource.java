package com.github.saphyra.apphub.service.skyxplore.game.domain.data.allocated_resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class AllocatedResource {
    @NonNull
    private final UUID allocatedResourceId;

    @NonNull
    private final UUID location;

    @NonNull
    private final UUID externalReference;

    @NonNull
    private final String dataId;

    private int amount;

    public void increaseAmount(int amount) {
        this.amount += amount;
    }
}
