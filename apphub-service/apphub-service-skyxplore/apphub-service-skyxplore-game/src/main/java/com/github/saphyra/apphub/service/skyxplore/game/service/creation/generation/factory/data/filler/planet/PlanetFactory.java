package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.planet;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanetFactory {
    private final IdGenerator idGenerator;
    private final Random random;
    private final GameProperties gameProperties;

    public Planet create(SolarSystem solarSystem, int planetIndex, SkyXploreGameSettings settings, UUID ownerId, double orbitRadius) {
        return Planet.builder()
            .planetId(idGenerator.randomUuid())
            .solarSystemId(solarSystem.getSolarSystemId())
            .defaultName(String.format("%s %s", solarSystem.getDefaultName(), planetIndex + 1))
            .size(random.randInt(settings.getPlanetSize()))
            .orbitRadius(orbitRadius)
            .orbitSpeed(random.randDouble(gameProperties.getPlanet().getOrbitSpeed()))
            .owner(ownerId)
            .build();
    }
}
