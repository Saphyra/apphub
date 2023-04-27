package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemSeedFactory {
    private final Random random;

    UUID[] newSolarSystem(SkyXploreGameSettings settings, boolean atLeastOnePlanet) {
        Range<Integer> planetsPerSolarSystem = settings.getPlanetsPerSolarSystem();
        int min = atLeastOnePlanet ? Math.max(1, planetsPerSolarSystem.getMin()) : planetsPerSolarSystem.getMin();
        int max = Math.max(1, planetsPerSolarSystem.getMax());

        int planetCount = random.randInt(min, max);

        return new UUID[planetCount];
    }
}
