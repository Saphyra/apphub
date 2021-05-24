package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.SolarSystemConnectionResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Universe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class UniverseToMapConverter {
    private final SolarSystemResponseExtractor solarSystemResponseExtractor;
    private final SolarSystemConnectionResponseExtractor solarSystemConnectionResponseExtractor;

    MapResponse convert(Universe universe) {
        List<MapSolarSystemResponse> solarSystems = solarSystemResponseExtractor.getSolarSystems(universe);
        List<SolarSystemConnectionResponse> connections = solarSystemConnectionResponseExtractor.getConnections(universe);

        return MapResponse.builder()
            .universeSize(universe.getSize())
            .solarSystems(solarSystems)
            .connections(connections)
            .build();
    }
}
