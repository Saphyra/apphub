package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolarSystemGenerationService {
    private final PlayerAssigner playerAssigner;
    private final SolarSystemSeedFactory solarSystemSeedFactory;
    private final Random random;
    private final SolarSystemPlacerService solarSystemPlacerService;
    private final PlanetPlacerService planetPlacerService;

    public List<NewbornSolarSystem> generateSolarSystems(Collection<Player> players, SkyXploreGameSettings settings) {
        List<UUID[]> solarSystems = new ArrayList<>();

        players.forEach(player -> playerAssigner.assignPlayer(player, solarSystems, settings));

        Stream.generate(() -> solarSystemSeedFactory.newSolarSystem(settings, false))
            .limit(random.randInt(settings.getAdditionalSolarSystems()))
            .forEach(solarSystems::add);

        return solarSystemPlacerService.place(solarSystems)
            .entrySet()
            .stream()
            .map(entry -> planetPlacerService.placePlanets(entry.getKey(), entry.getValue()))
            .toList();
    }
}
