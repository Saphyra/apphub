package com.github.saphyra.apphub.service.skyxplore.game.tick.work;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CitizenEfficiencyCalculator {
    private final CompetencyProperties properties;

    public double calculateEfficiency(Citizen citizen, SkillType skillType) {
        double moraleMultiplier = calculateMoraleMultiplier(citizen.getMorale());
        int skillLevel = citizen.getSkills().get(skillType).getLevel();
        double skillMultiplier = 1 + skillLevel * properties.getSkillLevelMultiplier();
        double result = moraleMultiplier * skillMultiplier;
        log.trace("Efficiency of citizen {} is: {}", citizen, result);
        return result;
    }

    private double calculateMoraleMultiplier(int morale) {
        if (morale > properties.getMoraleEfficiencyDropUnder()) {
            log.trace("Citizen has {} morale, what is enough for 100% work efficiency", morale);
            return 1;
        }

        double result = properties.getMinMoraleEfficiency() * morale / properties.getMoraleEfficiencyDropUnder();
        log.trace("Citizen has {} morale, so the efficiency is {}", morale, result);
        return result;
    }

    @Data
    @Component
    static class CompetencyProperties {
        @Value("${game.citizen.maxMorale}")
        private Integer defaultMorale;

        @Value("${game.citizen.moraleEfficiencyDropUnder}")
        private Integer moraleEfficiencyDropUnder;

        @Value("${game.citizen.minMoraleEfficiency}")
        private Double minMoraleEfficiency;

        @Value("${game.citizen.skillLevelMultiplier}")
        private Double skillLevelMultiplier;
    }
}
