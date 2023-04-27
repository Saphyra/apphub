package com.github.saphyra.apphub.service.skyxplore.game.domain.data.building;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Building {
    @NonNull
    private final UUID buildingId;

    @NonNull
    private final UUID location;

    @NonNull
    private final UUID surfaceId;

    @NonNull
    private final String dataId;

    private volatile int level;

    public void increaseLevel() {
        level += 1;
    }
}
