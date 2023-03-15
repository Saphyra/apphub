package com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class Durability {//TODO sync with DurabilityModel
    private final UUID durabilityId;
    private final UUID externalReference;
    private final int maxHitPoints;
    private int currentHitPoints;
}
