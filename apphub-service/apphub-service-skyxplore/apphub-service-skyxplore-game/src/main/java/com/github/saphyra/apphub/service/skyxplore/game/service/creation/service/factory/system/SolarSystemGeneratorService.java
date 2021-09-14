package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.GameCreationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolarSystemGeneratorService {
    private final GameCreationProperties gameCreationProperties;
    private final Random random;
    private final SolarSystemPlacementService solarSystemPlacementService;
    private final SolarSystemCoordinateShifter solarSystemCoordinateShifter;
    private final ExpectedPlanetCountCalculator expectedPlanetCountCalculator;

    public List<SolarSystem> generateSolarSystems(UUID gameId, int playerCount, SkyXploreGameCreationSettingsRequest settings) {
        int expectedPlanetCount = expectedPlanetCountCalculator.calculateExpectedPlanetCount(playerCount, settings);
        Range<Integer> planetsPerSystemRange = gameCreationProperties.getPlanet()
            .getPlanetsPerSystem()
            .get(settings.getSystemSize());

        List<SolarSystem> solarSystems = new ArrayList<>();

        for (int totalGeneratedPlanetCount = 0; totalGeneratedPlanetCount < expectedPlanetCount; ) {
            int numberOfPlanetsToGenerate = Math.min(random.randInt(planetsPerSystemRange.getMin(), planetsPerSystemRange.getMax()), expectedPlanetCount - totalGeneratedPlanetCount);

            SolarSystem solarSystem = solarSystemPlacementService.placeSolarSystem(gameId, settings, solarSystems, numberOfPlanetsToGenerate);
            solarSystems.add(solarSystem);
            totalGeneratedPlanetCount += numberOfPlanetsToGenerate;
        }

        solarSystemCoordinateShifter.shiftCoordinates(solarSystems);

        return solarSystems;
    }
}
