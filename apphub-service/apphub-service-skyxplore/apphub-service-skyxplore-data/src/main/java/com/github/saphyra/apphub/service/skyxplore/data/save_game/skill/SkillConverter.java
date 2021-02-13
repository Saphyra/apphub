package com.github.saphyra.apphub.service.skyxplore.data.save_game.skill;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.SkillModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class SkillConverter extends ConverterBase<SkillEntity, SkillModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected SkillModel processEntityConversion(SkillEntity entity) {
        SkillModel model = new SkillModel();
        model.setId(uuidConverter.convertEntity(entity.getSkillId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.SKILL);
        model.setCitizenId(uuidConverter.convertEntity(entity.getCitizenId()));
        model.setSkillType(entity.getSkillType());
        model.setLevel(entity.getLevel());
        model.setExperience(entity.getExperience());
        model.setNextLevel(entity.getNextLevel());
        return model;
    }

    @Override
    protected SkillEntity processDomainConversion(SkillModel domain) {
        return SkillEntity.builder()
            .skillId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .citizenId(uuidConverter.convertDomain(domain.getCitizenId()))
            .skillType(domain.getSkillType())
            .level(domain.getLevel())
            .experience(domain.getExperience())
            .nextLevel(domain.getNextLevel())
            .build();
    }
}
