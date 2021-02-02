package com.github.saphyra.apphub.service.skyxplore.data.common;

import com.github.saphyra.apphub.api.skyxplore.model.SkyXploreCharacterModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.service.skyxplore.data.character.dao.SkyXploreCharacter;
import org.springframework.stereotype.Component;

@Component
public class SkyXploreCharacterModelConverter extends ConverterBase<SkyXploreCharacter, SkyXploreCharacterModel> {
    @Override
    protected SkyXploreCharacterModel processEntityConversion(SkyXploreCharacter entity) {
        return SkyXploreCharacterModel.builder()
            .id(entity.getUserId())
            .name(entity.getName())
            .build();
    }

    @Override
    protected SkyXploreCharacter processDomainConversion(SkyXploreCharacterModel domain) {
        return SkyXploreCharacter.builder()
            .userId(domain.getId())
            .name(domain.getName())
            .build();
    }
}
