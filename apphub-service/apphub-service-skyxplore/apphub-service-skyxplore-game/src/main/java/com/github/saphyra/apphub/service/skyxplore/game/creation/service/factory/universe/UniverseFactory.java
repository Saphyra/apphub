package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system.SystemConnectionFactory;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system.SolarSystemFactory;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SolarSystem;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.SystemConnection;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class UniverseFactory {
    private final UniverseSizeCalculator universeSizeCalculator;
    private final SolarSystemFactory starSystemFactory;
    private final SystemConnectionFactory systemConnectionFactory;

    public Universe create(int memberNum, SkyXploreGameCreationSettingsRequest settings) {
        log.info("Creating universe...");
        int universeSize = universeSizeCalculator.calculate(memberNum, settings.getUniverseSize());
        log.info("UniverseSize: {}", universeSize);

        Map<Coordinate, SolarSystem> systems = starSystemFactory.create(memberNum, universeSize, settings);
        List<SystemConnection> connections = systemConnectionFactory.create(systems.keySet());

        log.info("Universe created.");
        return Universe.builder()
            .size(universeSize)
            .systems(systems)
            .connections(connections)
            .build();
    }
}
