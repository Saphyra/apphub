package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Building {
    private final UUID buildingId;
    private final BuildingType buildingType;
    private Integer level;
}
