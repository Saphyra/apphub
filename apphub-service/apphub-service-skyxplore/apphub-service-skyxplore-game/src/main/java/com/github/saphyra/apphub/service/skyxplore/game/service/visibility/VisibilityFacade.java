package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.planet.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class VisibilityFacade {
    private final SolarSystemVisibilityService solarSystemVisibilityService;
    private final PlanetVisibilityService planetVisibilityService;

    public boolean isVisible(GameData gameData, UUID userId, UUID solarSystemId) {
        return solarSystemVisibilityService.isVisible(gameData, userId, solarSystemId);
    }

    public boolean isVisible(UUID userId, Planet planet) {
        return planetVisibilityService.isVisible(userId, planet);
    }
}
