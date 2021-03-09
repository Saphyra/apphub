package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_util.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SolarSystemNames;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
public class SolarSystemPlacingService {
    private final SolarSystemCoordinateProvider solarSystemCoordinateProvider;
    private final SolarSystemNames solarSystemNames;
    private final ExecutorServiceBean executorServiceBean;
    private final SolarSystemFactory solarSystemFactory;

    public Map<Coordinate, SolarSystem> create(int memberNum, int universeSize, SkyXploreGameCreationSettingsRequest settings) {
        log.info("Generating SolarSystems...");

        List<String> usedSystemNames = new ArrayList<>();

        Map<Coordinate, String> coordinates = solarSystemCoordinateProvider.getCoordinates(memberNum, universeSize, settings.getSystemAmount())
            .stream()
            .collect(Collectors.toMap(Function.identity(), o -> {
                String name = solarSystemNames.getRandomStarName(usedSystemNames);
                usedSystemNames.add(name);
                return name;
            }));

        Map<Coordinate, SolarSystem> result = executorServiceBean.processCollectionWithWait(coordinates.entrySet(), entry -> solarSystemFactory.create(settings, entry.getValue(), entry.getKey()))
            .stream()
            .collect(Collectors.toMap(SolarSystem::getCoordinate, Function.identity()));

        log.info("SolarSystems generated.");
        if (log.isDebugEnabled()) {
            coordinates.forEach((key, value) -> log.debug("SolarSystem generated with name {} and coordinate {}", value, key));
        }
        return result;
    }
}
