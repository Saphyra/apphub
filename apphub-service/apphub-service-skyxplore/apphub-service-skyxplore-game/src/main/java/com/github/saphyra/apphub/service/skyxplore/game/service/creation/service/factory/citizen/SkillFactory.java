package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SkillFactory {
    private final GameProperties properties;
    private final IdGenerator idGenerator;

    Skill create(SkillType skillType, UUID citizenId) {
        return Skill.builder()
            .skillId(idGenerator.randomUuid())
            .citizenId(citizenId)
            .skillType(skillType)
            .level(1)
            .experience(0)
            .nextLevel(properties.getCitizen().getSkill().getExperiencePerLevel())
            .build();
    }
}
