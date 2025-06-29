package com.github.saphyra.apphub.service.skyxplore.game.service.planet.population;

import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SkillMultiplierCalculator {
    private final GameProperties properties;

    double calculateSkillMultiplier(GameData gameData, UUID citizenId, SkillType skillType) {
        int skillLevel = gameData.getSkills()
            .findByCitizenIdAndSkillType(citizenId, skillType)
            .getLevel();

        return 1 + (skillLevel - 1) * properties.getCitizen()
            .getSkill()
            .getSkillLevelMultiplier();
    }
}
