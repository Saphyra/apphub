package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.system;

import com.github.saphyra.apphub.api.skyxplore.request.game_creation.SkyXploreGameCreationSettingsRequest;
import com.github.saphyra.apphub.lib.common_domain.Range;
import com.github.saphyra.apphub.lib.common_util.Random;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ExpectedPlanetCountCalculator {
    private final GameProperties gameCreationProperties;
    private final Random random;

    int calculateExpectedPlanetCount(int playerCount, SkyXploreGameCreationSettingsRequest settings) {
        Range<Double> planetCountMultiplierRange = gameCreationProperties.getSolarSystem()
            .getAmountMultiplier()
            .get(settings.getSystemAmount());

        return (int) Math.round(playerCount * random.randDouble(planetCountMultiplierRange.getMin(), planetCountMultiplierRange.getMax()));
    }
}
