package com.github.saphyra.apphub.service.skyxplore.game.creation.service.factory.citizen;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.StorageType;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.storage.StorageBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.creation.GameCreationProperties;
import com.github.saphyra.apphub.service.skyxplore.game.creation.service.RandomNameProvider;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CitizenFactory {
    private final StorageBuildingService storageBuildingService;
    private final IdGenerator idGenerator;
    private final RandomNameProvider randomNameProvider;
    private final GameCreationProperties properties;

    public void addCitizens(Planet planet) {
        int capacity = storageBuildingService.findByStorageType(StorageType.CITIZEN)
            .getCapacity();

        Stream.generate(idGenerator::randomUuid)
            .limit(capacity)
            .map(this::createCitizen)
            .forEach(citizen -> planet.getPopulation().put(citizen.getCitizenId(), citizen));
    }

    private Citizen createCitizen(UUID citizenId) {
        return Citizen.builder()
            .citizenId(citizenId)
            .name(randomNameProvider.getRandomName(Collections.emptyList()))
            .morale(100)
            .satiety(100)
            .skills(createSkills())
            .build();
    }

    private Map<SkillType, Skill> createSkills() {
        return Arrays.stream(SkillType.values())
            .map(this::createSkill)
            .collect(Collectors.toMap(Skill::getSkillType, Function.identity()));
    }

    private Skill createSkill(SkillType skillType) {
        return Skill.builder()
            .skillId(idGenerator.randomUuid())
            .skillType(skillType)
            .level(1)
            .experience(0)
            .nextLevel(properties.getSkill().getInitialNextLevel())
            .build();
    }
}
