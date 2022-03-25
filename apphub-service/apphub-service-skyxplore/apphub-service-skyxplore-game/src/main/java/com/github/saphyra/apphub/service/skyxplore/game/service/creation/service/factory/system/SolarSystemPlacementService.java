package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.system;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.planet.SystemPopulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemPlacementService {
    private final Random random;
    private final IdGenerator idGenerator;
    private final SolarSystemNames solarSystemNames;
    private final SystemPopulationService systemPopulationService;
    private final SolarSystemFactory solarSystemFactory;
    private final GameProperties gameCreationProperties;

    SolarSystem placeSolarSystem(UUID gameId, SkyXploreGameCreationSettingsRequest settings, List<SolarSystem> solarSystems, int numberOfPlanetsToGenerate) {
        UUID solarSystemId = idGenerator.randomUuid();
        List<String> usedSystemNames = solarSystems.stream()
            .map(SolarSystem::getDefaultName)
            .collect(Collectors.toList());
        String solarSystemName = solarSystemNames.getRandomStarName(usedSystemNames);

        Range<Integer> radiusRange = gameCreationProperties.getSolarSystem()
            .getRadius()
            .get(settings.getSystemSize());

        double systemRadiusMultiplier = Math.max(1, numberOfPlanetsToGenerate / 4d);
        int baseRadius = random.randInt(radiusRange.getMin(), radiusRange.getMax());
        int placementRadius = (int) Math.round(baseRadius * systemRadiusMultiplier);

        Map<UUID, Planet> planets = systemPopulationService.populateSystemWithPlanets(gameId, solarSystemId, solarSystemName, placementRadius, numberOfPlanetsToGenerate, settings);

        return solarSystemFactory.create(gameId, solarSystemId, solarSystemName, placementRadius, solarSystems, planets);
    }
}
