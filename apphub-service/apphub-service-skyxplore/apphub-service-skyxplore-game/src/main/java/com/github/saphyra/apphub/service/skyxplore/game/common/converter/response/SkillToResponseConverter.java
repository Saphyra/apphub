package com.github.saphyra.apphub.service.skyxplore.game.common.converter.response;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SkillToResponseConverter {
    Map<String, SkillResponse> getSkills(GameData gameData, UUID citizenId) {
        return gameData.getSkills()
            .getByCitizenId(citizenId)
            .stream()
            .collect(Collectors.toMap(skill -> skill.getSkillType().name(), this::convert));
    }

    private SkillResponse convert(Skill skill) {
        return SkillResponse.builder()
            .experience(skill.getExperience())
            .level(skill.getLevel())
            .nextLevel(skill.getNextLevel())
            .build();
    }
}
