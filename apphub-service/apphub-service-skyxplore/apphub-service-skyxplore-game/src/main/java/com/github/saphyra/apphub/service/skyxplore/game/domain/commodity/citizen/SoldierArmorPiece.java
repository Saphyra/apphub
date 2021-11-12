package com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SoldierArmorPiece {
    private final UUID entityId;
    private final String dataId;
    private final int maxDurability;
    private int currentDurability;
}
