package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.tick.cache.Assignment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MakeCitizenWorkService {
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;
    private final UseCitizenWorkPointsService useCitizenWorkPointsService;

    /*
    Spending citizen's strength to produce workPoints
     */
    public int requestWork(UUID gameId, UUID userId, UUID planetId, Assignment assignment, int requestedWorkPoints, SkillType skillType) {
        log.debug("Requesting {} workPoint of work of skillType {} on assignment {} in game {}", requestedWorkPoints, skillType, assignment, gameId);
        Citizen citizen = assignment.getCitizen();
        double citizenEfficiency = citizenEfficiencyCalculator.calculateEfficiency(citizen, skillType);

        int availableWorkPoints = (int) Math.round(assignment.getWorkPointsLeft() * citizenEfficiency);

        int workPointsToComplete = Math.min(availableWorkPoints, requestedWorkPoints);
        log.debug("requestedWorkPoints: {}, workPointsToComplete: {}", requestedWorkPoints, workPointsToComplete);

        int workPointsUsed = (int) Math.round(workPointsToComplete / citizenEfficiency);
        assignment.setWorkPointsLeft(assignment.getWorkPointsLeft() - workPointsUsed);

        useCitizenWorkPointsService.useWorkPoints(gameId, userId, planetId, citizen, workPointsUsed, skillType);

        log.debug("Assignment after work completed: {} in game {}", assignment, gameId);

        return workPointsToComplete;
    }
}
