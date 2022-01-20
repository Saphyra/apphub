package com.github.saphyra.apphub.service.skyxplore.game.common.converter.response;

import com.github.saphyra.apphub.api.skyxplore.response.game.planet.SkillResponse;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SkillToResponseConverter {
    Map<String, SkillResponse> getSkills(Map<SkillType, Skill> skills) {
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
