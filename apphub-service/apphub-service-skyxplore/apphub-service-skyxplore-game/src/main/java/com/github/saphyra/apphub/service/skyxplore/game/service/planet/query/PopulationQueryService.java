package com.github.saphyra.apphub.service.skyxplore.game.service.planet.query;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.CitizenResponse;
import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.common.GameDao;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Citizen;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PopulationQueryService {
    private final GameDao gameDao;

    public List<CitizenResponse> getPopulation(UUID userId, UUID planetId) {
        return gameDao.findByUserIdValidated(userId)
            .getUniverse()
            .findPlanetByIdValidated(planetId)
            .getPopulation()
            .values()
            .stream()
            .map(this::convert)
            .collect(Collectors.toList());
    }

    private CitizenResponse convert(Citizen citizen) {
        return CitizenResponse.builder()
            .citizenId(citizen.getCitizenId())
            .name(citizen.getName())
            .morale(citizen.getMorale())
            .satiety(citizen.getSatiety())
            .skills(getSkills(citizen.getSkills()))
            .build();
    }

    private Map<String, SkillResponse> getSkills(Map<SkillType, Skill> skills) {
        return skills.entrySet()
            .stream()
            .collect(Collectors.toMap(entry -> entry.getKey().name(), entry -> convert(entry.getValue())));
    }

    private SkillResponse convert(Skill skill) {
        return SkillResponse.builder()
            .experience(skill.getExperience())
            .level(skill.getLevel())
            .nextLevel(skill.getNextLevel())
            .build();
    }
}
