package com.github.saphyra.apphub.service.skyxplore.lobby.service.settings;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreGameSettings;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkyXploreGameSettingsValidator {
    void validate(SkyXploreGameSettings settings) {
        ValidationUtil.notNull(settings.getMaxPlayersPerSolarSystem(), "maxPlayersPerSolarSystem");

        ValidationUtil.notNull(settings.getAdditionalSolarSystems(), "additionalSolarSystems");
        ValidationUtil.atLeast(settings.getAdditionalSolarSystems().getMin(), 0, "additionalSolarSystems.min");
        ValidationUtil.maximum(settings.getAdditionalSolarSystems().getMax(), 30, "additionalSolarSystems.max");
        ValidationUtil.atLeast(settings.getAdditionalSolarSystems().getMax(), settings.getAdditionalSolarSystems().getMin(), "additionalSolarSystems.max");

        ValidationUtil.notNull(settings.getPlanetsPerSolarSystem(), "planetsPerSolarSystem");
        ValidationUtil.atLeast(settings.getPlanetsPerSolarSystem().getMin(), 0, "planetsPerSolarSystem.min");
        ValidationUtil.maximum(settings.getPlanetsPerSolarSystem().getMax(), 10, "planetsPerSolarSystem.max");
        ValidationUtil.atLeast(settings.getPlanetsPerSolarSystem().getMax(), settings.getPlanetsPerSolarSystem().getMin(), "planetsPerSolarSystem.max");

        ValidationUtil.notNull(settings.getPlanetSize(), "planetSize");
        ValidationUtil.atLeast(settings.getPlanetSize().getMin(), 5, "planetSize.min");
        ValidationUtil.maximum(settings.getPlanetSize().getMax(), 15, "planetSize.max");
        ValidationUtil.atLeast(settings.getPlanetSize().getMax(), settings.getPlanetSize().getMin(), "planetSize.max");
    }
}
