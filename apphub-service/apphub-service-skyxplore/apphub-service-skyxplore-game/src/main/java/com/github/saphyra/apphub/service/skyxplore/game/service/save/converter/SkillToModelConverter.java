package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillToModelConverter {
    public List<SkillModel> convert(UUID gameId, Collection<Skill> skills) {
        return skills.stream()
            .map(skill -> convert(gameId, skill))
            .collect(Collectors.toList());
    }

    public SkillModel convert(UUID gameId, Skill skill) {
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
}
