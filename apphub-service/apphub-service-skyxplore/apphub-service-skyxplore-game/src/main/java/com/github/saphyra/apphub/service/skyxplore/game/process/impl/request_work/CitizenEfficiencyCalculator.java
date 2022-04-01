package com.github.saphyra.apphub.service.skyxplore.game.process.impl.request_work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenMoraleProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenEfficiencyCalculator {
    private final GameProperties properties;

    public double calculateEfficiency(Citizen citizen, SkillType skillType) {
        double moraleMultiplier = calculateMoraleMultiplier(citizen.getMorale());
        int skillLevel = citizen.getSkills()
            .get(skillType)
            .getLevel();
        double skillMultiplier = 1 + skillLevel * properties.getCitizen()
            .getSkill()
            .getSkillLevelMultiplier();
        double result = moraleMultiplier * skillMultiplier;
        log.trace("Efficiency of citizen {} is: {}", citizen, result);
        return result;
    }

    private double calculateMoraleMultiplier(int morale) {
        CitizenMoraleProperties moraleProperties = properties.getCitizen()
            .getMorale();
        if (morale > moraleProperties.getWorkEfficiencyDropUnder()) {
            log.trace("Citizen has {} morale, what is enough for 100% work efficiency", morale);
            return 1;
        }

        double result = moraleProperties.getMinEfficiency() * morale / moraleProperties.getWorkEfficiencyDropUnder();
        log.trace("Citizen has {} morale, so the efficiency is {}", morale, result);
        return result;
    }
}
