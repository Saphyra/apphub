package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SolarSystem {
    private final UUID solarSystemId;
    private final int radius;
    private final String systemName; //TODO allow users to rename systems
    private final Coordinate coordinate;
    private final Map<UUID, Planet> planets;
}
