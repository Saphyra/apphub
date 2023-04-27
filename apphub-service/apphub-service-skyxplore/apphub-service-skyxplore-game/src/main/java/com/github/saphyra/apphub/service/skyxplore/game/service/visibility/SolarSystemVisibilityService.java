package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemVisibilityService {
    private final PlanetVisibilityService planetVisibilityService;

    boolean isVisible(GameData gameData, UUID userId, UUID solarSystemId) {
        return gameData.getPlanets()
            .getBySolarSystemId(solarSystemId)
            .stream()
            .anyMatch(planet -> planetVisibilityService.isVisible(userId, planet));
    }
}
