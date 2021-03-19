package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.universe;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.geometry.Coordinate;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system.SolarSystemPlacingService;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.system_connection.SystemConnectionProvider;
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
public class UniverseFactory {
    private final UniverseSizeCalculator universeSizeCalculator;
    private final SolarSystemPlacingService starSystemFactory;
    private final SystemConnectionProvider systemConnectionProvider;

    public Universe create(int memberNum, SkyXploreGameCreationSettingsRequest settings) {
        log.info("Creating universe...");
        int universeSize = universeSizeCalculator.calculate(memberNum, settings.getUniverseSize());
        log.info("UniverseSize: {}", universeSize);

        Map<Coordinate, SolarSystem> systems = starSystemFactory.create(memberNum, universeSize, settings);
        List<SystemConnection> connections = systemConnectionProvider.getConnections(systems.keySet());

        log.info("Universe created.");
        return Universe.builder()
            .size(universeSize)
            .systems(systems)
            .connections(connections)
            .build();
    }
}
