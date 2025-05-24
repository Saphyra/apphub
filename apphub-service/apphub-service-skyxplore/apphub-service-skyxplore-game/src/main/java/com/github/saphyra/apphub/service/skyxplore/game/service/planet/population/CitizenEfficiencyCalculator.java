package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenEfficiencyCalculator {
    private final MoraleMultiplierCalculator moraleMultiplierCalculator;
    private final SkillMultiplierCalculator skillMultiplierCalculator;

    public double calculateEfficiency(GameData gameData, Citizen citizen, SkillType skillType) {
        double moraleMultiplier = moraleMultiplierCalculator.calculateMoraleMultiplier(citizen.getMorale());

        double skillMultiplier = skillMultiplierCalculator.calculateSkillMultiplier(gameData, citizen.getCitizenId(), skillType);
        double result = moraleMultiplier * skillMultiplier;
        log.trace("Efficiency of citizen {} is: {}", citizen, result);
        return result;
    }

    public Integer calculateMoraleRequirement(GameData gameData, Citizen citizen, SkillType skillType, int requestedWorkPoints) {
        double skillMultiplier = skillMultiplierCalculator.calculateSkillMultiplier(gameData, citizen.getCitizenId(), skillType);

        return (int) Math.ceil(requestedWorkPoints / skillMultiplier);
    }
}
