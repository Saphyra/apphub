package com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.service.RandomNameProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.LocationType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
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
    private final GameProperties properties;
    private final SoldierDataFactory soldierDataFactory;

    Citizen create(UUID planetId) {
        CitizenProperties citizenProperties = properties.getCitizen();

        UUID citizenId = idGenerator.randomUuid();
        return Citizen.builder()
            .citizenId(citizenId)
            .name(randomNameProvider.getRandomName(Collections.emptyList()))
            .location(planetId)
            .locationType(LocationType.PLANET)
            .morale(citizenProperties.getMorale().getMax())
            .satiety(citizenProperties.getSatiety().getMax())
            .skills(createSkills(citizenId))
            .soldierData(soldierDataFactory.create())
            .build();
    }

    private Map<SkillType, Skill> createSkills(UUID citizenId) {
        return Arrays.stream(SkillType.values())
            .map(skillType -> skillFactory.create(skillType, citizenId))
            .collect(Collectors.toMap(Skill::getSkillType, Function.identity(), (skill, skill2) -> skill));
    }
}
