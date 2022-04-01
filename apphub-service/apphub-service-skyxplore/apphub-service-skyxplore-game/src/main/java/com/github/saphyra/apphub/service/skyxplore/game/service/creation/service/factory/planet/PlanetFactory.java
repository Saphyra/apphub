package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet;

import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.common.CoordinateModelFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SurfaceMap;
import com.github.saphyra.apphub.service.skyxplore.game.service.common.factory.StorageDetailsFactory;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.surface.SurfaceFactory;
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
    private final CoordinateModelFactory coordinateModelFactory;
    private final StorageDetailsFactory storageDetailsFactory;

    Planet create(UUID gameId, Integer planetIndex, Coordinate coordinate, UUID solarSystemId, String systemName, Range<Integer> planetSizeRange) {
        int planetSize = random.randInt(planetSizeRange.getMin(), planetSizeRange.getMax());
        UUID planetId = idGenerator.randomUuid();

        Map<Coordinate, Surface> surfaces = surfaceFactory.create(gameId, planetId, planetSize);

        return Planet.builder()
            .planetId(planetId)
            .solarSystemId(solarSystemId)
            .coordinate(coordinateModelFactory.create(coordinate, gameId, planetId))
            .defaultName(String.format("%s %s", systemName, ALPHABET.charAt(planetIndex)))
            .size(planetSize)
            .surfaces(new SurfaceMap(surfaces))
            .storageDetails(storageDetailsFactory.create(gameId, planetId, LocationType.PLANET)) //TODO unit test
            .build();
    }
}
