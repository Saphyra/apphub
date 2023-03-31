package com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Durability {
    @NonNull
    private final UUID durabilityId;

    @NonNull
    private final UUID externalReference;

    @NonNull
    private final Integer maxHitPoints;

    @NonNull
    private Integer currentHitPoints;
}
