package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.newborn_solar_system;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemSelector {
    private final SuitableSolarSystemFinder suitableSolarSystemFinder;
    private final Random random;

    Optional<UUID[]> selectSolarSystem(List<UUID[]> solarSystems, SkyXploreGameSettings settings) {
        List<UUID[]> possibilities = new ArrayList<>(suitableSolarSystemFinder.getSuitableSolarSystems(solarSystems, settings));
        possibilities.add(null);

        return Optional.ofNullable(possibilities.get(random.randInt(0, possibilities.size() - 1)));
    }
}
