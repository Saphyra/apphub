package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.CitizenConverter;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.SkillConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CitizenUpdateService {
    private final GameProperties gameProperties;
    private final CitizenEfficiencyCalculator citizenEfficiencyCalculator;
    private final CitizenConverter citizenConverter;
    private final SkillConverter skillConverter;

    public void updateCitizen(GameProgressDiff progressDiff, GameData gameData, UUID citizenId, int workPoints, SkillType skillType) {
        Citizen citizen = gameData.getCitizens()
            .findByCitizenIdValidated(citizenId);
        log.info("Citizen {} in game {} used {} workPoints of skill {}", citizen, gameData.getGameId(), workPoints, skillType);

        int moraleToReduce = citizenEfficiencyCalculator.calculateMoraleRequirement(gameData, citizen, skillType, workPoints);

        citizen.reduceMorale(moraleToReduce);

        Skill skill = gameData.getSkills()
            .findByCitizenIdAndSkillType(citizenId, skillType);

        skill.increaseExperience(moraleToReduce);
        if (skill.getExperience() >= skill.getNextLevel()) {
            log.info("Skill {} level earned for citizen {} in game {}", skillType, citizen.getCitizenId(), gameData.getGameId());
            skill.increaseLevel();
            skill.setExperience(skill.getExperience() - skill.getNextLevel());
            int experiencePerLevel = gameProperties.getCitizen()
                .getSkill()
                .getExperiencePerLevel();
            skill.setNextLevel(skill.getLevel() * experiencePerLevel);
        }

        progressDiff.save(citizenConverter.toModel(gameData.getGameId(), citizen));
        progressDiff.save(skillConverter.toModel(gameData.getGameId(), skill));
    }
}
