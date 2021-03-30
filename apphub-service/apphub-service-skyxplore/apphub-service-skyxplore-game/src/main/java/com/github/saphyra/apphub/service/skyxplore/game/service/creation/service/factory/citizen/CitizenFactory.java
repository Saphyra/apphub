package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.RandomNameProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenFactory {
    private final IdGenerator idGenerator;
    private final RandomNameProvider randomNameProvider;
    private final SkillFactory skillFactory;

    Citizen create(UUID planetId) {
        UUID citizenId = idGenerator.randomUuid();
        return Citizen.builder()
            .citizenId(citizenId)
            .name(randomNameProvider.getRandomName(Collections.emptyList()))
            .location(planetId)
            .locationType(LocationType.PLANET)
            .morale(100)
            .satiety(100)
            .skills(createSkills(citizenId))
            .build();
    }

    private Map<SkillType, Skill> createSkills(UUID citizenId) {
        return Arrays.stream(SkillType.values())
            .map(skillType -> skillFactory.create(skillType, citizenId))
            .collect(Collectors.toMap(Skill::getSkillType, Function.identity(), (skill, skill2) -> skill));
    }
}
