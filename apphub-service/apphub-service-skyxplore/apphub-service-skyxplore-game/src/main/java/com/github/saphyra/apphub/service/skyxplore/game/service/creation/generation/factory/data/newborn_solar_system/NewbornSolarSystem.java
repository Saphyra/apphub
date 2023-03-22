package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.lib.geometry.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class NewbornSolarSystem {
    private final Map<Double, UUID> planets;
    private int radius;
    private final Coordinate coordinate;
}
