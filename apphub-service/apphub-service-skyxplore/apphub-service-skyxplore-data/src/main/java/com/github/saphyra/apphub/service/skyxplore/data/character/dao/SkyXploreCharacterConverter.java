package com.github.saphyra.apphub.service.skyxplore.data.character.dao;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkyXploreCharacterConverter extends ConverterBase<SkyXploreCharacterEntity, SkyXploreCharacter> {
    private final UuidConverter uuidConverter;

    @Override
    protected SkyXploreCharacter processEntityConversion(SkyXploreCharacterEntity entity) {
        return SkyXploreCharacter.builder()
            .userId(uuidConverter.convertEntity(entity.getUserId()))
            .name(entity.getName())
            .build();
    }

    @Override
    protected SkyXploreCharacterEntity processDomainConversion(SkyXploreCharacter domain) {
        return SkyXploreCharacterEntity.builder()
            .userId(uuidConverter.convertDomain(domain.getUserId()))
            .name(domain.getName())
            .build();
    }
}
