package com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.api.skyxplore.response.game.citizen.SkillResponse;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameDataToModelConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillConverter implements GameDataToModelConverter {
    public List<SkillModel> toModel(UUID gameId, Collection<Skill> skills) {
        return skills.stream()
            .map(skill -> toModel(gameId, skill))
            .collect(Collectors.toList());
    }

    public SkillModel toModel(UUID gameId, Skill skill) {
        SkillModel model = new SkillModel();
        model.setId(skill.getSkillId());
        model.setGameId(gameId);
        model.setType(GameItemType.SKILL);
        model.setCitizenId(skill.getCitizenId());
        model.setSkillType(skill.getSkillType().name());
        model.setLevel(skill.getLevel());
        model.setExperience(skill.getExperience());
        model.setNextLevel(skill.getNextLevel());
        return model;
    }

    public Map<String, SkillResponse> toResponse(GameData gameData, UUID citizenId) {
        return gameData.getSkills()
            .getByCitizenId(citizenId)
            .stream()
            .collect(Collectors.toMap(skill -> skill.getSkillType().name(), this::toResponse));
    }

    private SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
            .experience(skill.getExperience())
            .level(skill.getLevel())
            .nextLevel(skill.getNextLevel())
            .build();
    }

    @Override
    public List<SkillModel> convert(UUID gameId, GameData gameData) {
        return toModel(gameId, gameData.getSkills());
    }
}
