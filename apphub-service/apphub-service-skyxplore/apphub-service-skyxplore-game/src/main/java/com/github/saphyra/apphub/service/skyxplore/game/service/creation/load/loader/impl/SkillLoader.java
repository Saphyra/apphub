package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader.impl;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.skill.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SkillLoader extends AutoLoader<SkillModel, Skill> {
    public SkillLoader(GameItemLoader gameItemLoader) {
        super(gameItemLoader);
    }

    @Override
    protected GameItemType getGameItemType() {
        return GameItemType.SKILL;
    }

    @Override
    protected Class<SkillModel[]> getArrayClass() {
        return SkillModel[].class;
    }

    @Override
    protected void addToGameData(GameData gameData, List<Skill> items) {
        gameData.getSkills()
            .addAll(items);
    }

    @Override
    protected Skill convert(SkillModel model) {
        return Skill.builder()
            .skillId(model.getId())
            .citizenId(model.getCitizenId())
            .skillType(SkillType.valueOf(model.getSkillType()))
            .level(model.getLevel())
            .experience(model.getExperience())
            .nextLevel(model.getNextLevel())
            .build();
    }
}
