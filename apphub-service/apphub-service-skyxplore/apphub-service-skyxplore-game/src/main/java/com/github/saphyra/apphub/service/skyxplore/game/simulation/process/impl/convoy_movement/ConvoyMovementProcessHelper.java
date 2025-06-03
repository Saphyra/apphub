package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.convoy_movement;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenEfficiencyCalculator;
import com.github.saphyra.apphub.service.skyxplore.game.service.planet.population.CitizenUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class ConvoyMovementProcessHelper {
    private final GameProperties gameProperties;
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;
    private final CitizenUpdateService citizenUpdateService;

    int getWorkPointsPerTick(GameData gameData, UUID citizenId, int requestedWorkPoints) {
        int defaultWorkPointsPerTick = gameProperties.getCitizen()
            .getWorkPointsPerTick();

        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);

        double citizenEfficiency = citizenEfficiencyCalculator.calculateEfficiency(gameData, citizen, SkillType.LOGISTICS);

        int maxWorkPointsPerTick = Math.toIntExact(Math.round(defaultWorkPointsPerTick * citizenEfficiency));

        return Math.min(requestedWorkPoints, maxWorkPointsPerTick);
    }

    public void work(GameProgressDiff progressDiff, GameData gameData, UUID citizenId, SkillType skillType, int workToBeDone) {
        citizenUpdateService.updateCitizen(progressDiff, gameData, citizenId, workToBeDone, skillType);
    }
}
