package com.github.saphyra.apphub.service.skyxplore.game.service.visibility;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.solar_system.SolarSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SolarSystemVisibilityService {
    private final PlanetVisibilityService planetVisibilityService;

    boolean isVisible(UUID userId, SolarSystem solarSystem) {
        return solarSystem.getPlanets()
            .values()
            .stream()
            .anyMatch(planet -> planetVisibilityService.isVisible(userId, planet));
    }
}
