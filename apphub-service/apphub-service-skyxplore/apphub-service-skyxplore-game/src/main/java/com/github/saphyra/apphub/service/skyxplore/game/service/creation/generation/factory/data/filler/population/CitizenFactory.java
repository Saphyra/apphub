package com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.factory.data.filler.population;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenHitPointsProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.CitizenProperties;
import com.github.saphyra.apphub.service.skyxplore.game.config.properties.GameProperties;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.citizen.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.durability.Durability;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.generation.RandomNameProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
class CitizenFactory {
    private final IdGenerator idGenerator;
    private final RandomNameProvider randomNameProvider;
    private final SkillFactory skillFactory;
    private final GameProperties properties;

    void addToGameData(UUID location, GameData gameData) {
        CitizenProperties citizenProperties = properties.getCitizen();

        Citizen citizen = Citizen.builder()
            .citizenId(idGenerator.randomUuid())
            .name(randomNameProvider.getRandomName(Collections.emptyList()))
            .location(location)
            .morale(citizenProperties.getMorale().getMax())
            .satiety(citizenProperties.getSatiety().getMax())
            .build();

        gameData.getCitizens()
            .add(citizen);

        addSkills(citizen.getCitizenId(), gameData);
        addSoldierData(citizen.getCitizenId(), gameData);
    }

    private void addSoldierData(UUID citizenId, GameData gameData) {
        CitizenHitPointsProperties hitPointsProperties = properties.getCitizen()
            .getHitPoints();
        int hitPoints = hitPointsProperties.getPerLevel() + hitPointsProperties.getBase();

        Durability durability = Durability.builder()
            .durabilityId(idGenerator.randomUuid())
            .externalReference(citizenId)
            .maxHitPoints(hitPoints)
            .currentHitPoints(hitPoints)
            .build();

        gameData.getDurabilities()
            .add(durability);
    }

    private void addSkills(UUID citizenId, GameData gameData) {
        Arrays.stream(SkillType.values())
            .map(skillType -> skillFactory.create(skillType, citizenId))
            .forEach(skill -> gameData.getSkills().add(skill));
    }
}
