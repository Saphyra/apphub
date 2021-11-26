package com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.loader;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.SkillType;
import com.github.saphyra.apphub.service.skyxplore.game.domain.commodity.citizen.Skill;
import com.github.saphyra.apphub.service.skyxplore.game.service.creation.load.GameItemLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class SkillLoader {
    private final GameItemLoader gameItemLoader;

    Map<SkillType, Skill> load(UUID citizenId) {
        List<SkillModel> models = gameItemLoader.loadChildren(citizenId, GameItemType.SKILL, SkillModel[].class);
        return models.stream()
            .map(this::convert)
            .collect(Collectors.toMap(Skill::getSkillType, Function.identity()));
    }

    private Skill convert(SkillModel model) {
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
