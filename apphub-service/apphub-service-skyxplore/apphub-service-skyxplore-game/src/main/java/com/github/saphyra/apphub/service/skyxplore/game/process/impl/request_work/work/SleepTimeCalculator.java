package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.CitizenEfficiencyCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SleepTimeCalculator {
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;
    private final GameProperties gameProperties;

    long calculateSleepTime(Planet planet, UUID citizenId, SkillType skillType, int workPoints) {
        int workPointsPerSeconds = gameProperties.getCitizen()
            .getWorkPointsPerSeconds();
        Citizen citizen = planet.getPopulation()
            .get(citizenId);
        double workPointsPerSecond = workPointsPerSeconds * citizenEfficiencyCalculator.calculateEfficiency(citizen, skillType);

        long result = Math.round(workPoints / workPointsPerSecond * 1000);
        log.info("Citizen {} will work {} milliseconds to achieve {} workPoints.", citizenId, result, workPoints);
        return result;
    }
}
