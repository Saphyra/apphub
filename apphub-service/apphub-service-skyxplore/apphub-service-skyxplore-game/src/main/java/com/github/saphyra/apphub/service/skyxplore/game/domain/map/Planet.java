package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Human;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Planet {
    private final UUID planetId;
    private final UUID solarSystemId;
    private final String planetName; //TODO allow users to rename planets
    private final Coordinate coordinate;
    private final int size;
    private final Map<Coordinate, Surface> surfaces;
    private UUID owner;
    @Builder.Default
    private Map<UUID, Human> population = new HashMap<>();
}
