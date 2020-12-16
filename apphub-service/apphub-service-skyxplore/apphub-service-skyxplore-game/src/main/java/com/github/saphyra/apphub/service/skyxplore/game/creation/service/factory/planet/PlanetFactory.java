package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.planet;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Surface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PlanetFactory {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVXYZ";

    private final IdGenerator idGenerator;
    private final Random random;
    private final GameCreationProperties properties;
    private final SurfaceFactory surfaceFactory;

    public Map<UUID, Planet> create(String systemName, SkyXploreGameCreationSettingsRequest settings) {
        log.debug("Generating planets...");
        Range<Integer> range = properties.getPlanet().getSystemSize().get(settings.getSystemSize());

        Map<UUID, Planet> result = new HashMap<>();
        int expectedPlanetAmount = random.randInt(range.getMin(), range.getMax());
        for (int i = 1; i <= expectedPlanetAmount; i++) {
            Range<Integer> planetSizeRange = properties.getPlanet().getPlanetSize().get(settings.getPlanetSize());
            int planetSize = random.randInt(planetSizeRange.getMin(), planetSizeRange.getMax());

            Map<Coordinate, Surface> surfaces = surfaceFactory.create(planetSize);

            Planet planet = Planet.builder()
                .planetId(idGenerator.randomUuid())
                .planetName(String.format("%s %s", systemName, ALPHABET.charAt(i - 1)))
                .size(planetSize)
                .surfaces(surfaces)
                .build();

            result.put(planet.getPlanetId(), planet);
        }
        log.debug("Planets generated.");
        return result;
    }
}
