package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction_area;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionAreaModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
//TODO unit test
class ConstructionAreaConverter extends ConverterBase<ConstructionAreaEntity, ConstructionAreaModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ConstructionAreaEntity processDomainConversion(ConstructionAreaModel domain) {
        return ConstructionAreaEntity.builder()
            .constructionAreaId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .surfaceId(uuidConverter.convertDomain(domain.getSurfaceId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .dataId(domain.getDataId())
            .build();
    }

    @Override
    protected ConstructionAreaModel processEntityConversion(ConstructionAreaEntity entity) {
        ConstructionAreaModel model = new ConstructionAreaModel();
        model.setId(uuidConverter.convertEntity(entity.getConstructionAreaId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.CONSTRUCTION_AREA);
        model.setSurfaceId(uuidConverter.convertEntity(entity.getSurfaceId()));
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setDataId(entity.getDataId());
        return model;
    }
}
