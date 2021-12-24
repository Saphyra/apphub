package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.construction;

import com.github.saphyra.apphub.api.skyxplore.model.game.ConstructionModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ConstructionConverter extends ConverterBase<ConstructionEntity, ConstructionModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected ConstructionModel processEntityConversion(ConstructionEntity entity) {
        ConstructionModel model = new ConstructionModel();
        model.setId(uuidConverter.convertEntity(entity.getConstructionId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.CONSTRUCTION);
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setLocationType(entity.getLocationType());
        model.setRequiredWorkPoints(entity.getRequiredWorkPoints());
        model.setCurrentWorkPoints(entity.getCurrentWorkPoints());
        model.setPriority(entity.getPriority());
        return model;
    }

    @Override
    protected ConstructionEntity processDomainConversion(ConstructionModel domain) {
        return ConstructionEntity.builder()
            .constructionId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .locationType(domain.getLocationType())
            .requiredWorkPoints(domain.getRequiredWorkPoints())
            .currentWorkPoints(domain.getCurrentWorkPoints())
            .priority(domain.getPriority())
            .build();
    }
}
