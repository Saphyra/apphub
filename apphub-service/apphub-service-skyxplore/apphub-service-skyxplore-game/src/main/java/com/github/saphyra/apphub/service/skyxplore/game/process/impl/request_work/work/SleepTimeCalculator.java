package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
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

    long calculateSleepTime(GameData gameData, UUID citizenId, SkillType skillType, int workPoints) {
        int workPointsPerSeconds = gameProperties.getCitizen()
            .getWorkPointsPerSeconds();
        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);
        double workPointsPerSecond = workPointsPerSeconds * citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, skillType);

        long result = Math.round(workPoints / workPointsPerSecond * 1000);
        log.info("Citizen {} will work {} milliseconds to achieve {} workPoints.", citizenId, result, workPoints);
        return result;
    }
}
