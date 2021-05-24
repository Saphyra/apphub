package com.github.saphyra.apphub.service.skyxplore.data.save_game.universe;

import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.api.skyxplore.model.game.UniverseModel;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class UniverseConverter extends ConverterBase<UniverseEntity, UniverseModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected UniverseModel processEntityConversion(UniverseEntity entity) {
        UniverseModel model = new UniverseModel();
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setSize(entity.getSize());
        model.setType(GameItemType.UNIVERSE);
        return model;
    }

    @Override
    protected UniverseEntity processDomainConversion(UniverseModel domain) {
        return UniverseEntity.builder()
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .size(domain.getSize())
            .build();
    }
}
