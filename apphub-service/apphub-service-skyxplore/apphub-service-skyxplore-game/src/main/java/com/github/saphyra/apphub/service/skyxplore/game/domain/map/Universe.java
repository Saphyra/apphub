package com.github.saphyra.apphub.service.skyxplore.game.domain.map;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Universe {
    private final int size;
    private final Map<Coordinate, SolarSystem> systems;
    private final List<SystemConnection> connections;
}
