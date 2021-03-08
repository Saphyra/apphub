package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.surface.SurfaceFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class PlanetFactory {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVXYZ";

    private final IdGenerator idGenerator;
    private final Random random;
    private final SurfaceFactory surfaceFactory;

    Planet create(Integer planetIndex, Coordinate coordinate, UUID solarSystemId, String systemName, Range<Integer> planetSizeRange) {
        int planetSize = random.randInt(planetSizeRange.getMin(), planetSizeRange.getMax());
        UUID planetId = idGenerator.randomUuid();

        Map<Coordinate, Surface> surfaces = surfaceFactory.create(planetId, planetSize);

        return Planet.builder()
            .planetId(planetId)
            .solarSystemId(solarSystemId)
            .coordinate(coordinate)
            .defaultName(String.format("%s %s", systemName, ALPHABET.charAt(planetIndex)))
            .size(planetSize)
            .surfaces(surfaces)
            .build();
    }
}
