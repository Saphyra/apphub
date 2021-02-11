package com.github.saphyra.apphub.service.skyxplore.game.service.save.converter;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class SkillToModelConverter {
    public List<SkillModel> convert(Collection<Skill> skills, Game game) {
        return skills.stream()
            .map(skill -> convert(skill, game))
            .collect(Collectors.toList());
    }

    public SkillModel convert(Skill skill, Game game) {
        SkillModel model = new SkillModel();
        model.setId(skill.getSkillId());
        model.setGameId(game.getGameId());
        model.setType(GameItemType.SKILL);
        model.setCitizenId(skill.getCitizenId());
        model.setSkillType(skill.getSkillType().name());
        model.setLevel(skill.getLevel());
        model.setExperience(skill.getExperience());
        model.setNextLevel(skill.getNextLevel());
        return model;
    }
}
