package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class SkillFactory {
    private final GameCreationProperties properties;
    private final IdGenerator idGenerator;

    Skill create(SkillType skillType, UUID citizenId) {
        return Skill.builder()
            .skillId(idGenerator.randomUuid())
            .citizenId(citizenId)
            .skillType(skillType)
            .level(1)
            .experience(0)
            .nextLevel(properties.getSkill().getInitialNextLevel())
            .build();
    }
}
