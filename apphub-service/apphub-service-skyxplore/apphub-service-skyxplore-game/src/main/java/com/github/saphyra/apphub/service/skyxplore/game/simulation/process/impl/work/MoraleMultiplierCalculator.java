package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.work;

import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class MoraleMultiplierCalculator {
    private final GameProperties properties;

    double calculateMoraleMultiplier(int morale) {
        CitizenMoraleProperties moraleProperties = properties.getCitizen()
            .getMorale();
        if (morale > moraleProperties.getWorkEfficiencyDropUnder()) {
            log.trace("Citizen has {} morale, what is enough for 100% work efficiency", morale);
            return 1;
        }

        double result = Math.max(moraleProperties.getMinEfficiency(), (double) morale / moraleProperties.getWorkEfficiencyDropUnder());
        log.trace("Citizen has {} morale, so the efficiency is {}", morale, result);
        return result;
    }
}
