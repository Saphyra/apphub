package com.github.saphyra.apphub.service.skyxplore.game.service.map.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.map.MapSolarSystemResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class UniverseToMapConverter {
    private final SolarSystemResponseExtractor solarSystemResponseExtractor;

    MapResponse convert(UUID userId, GameData gameData) {
        List<MapSolarSystemResponse> solarSystems = solarSystemResponseExtractor.getSolarSystems(userId, gameData);

        return MapResponse.builder()
            .universeSize(gameData.getUniverseSize())
            .solarSystems(solarSystems)
            .build();
    }
}
