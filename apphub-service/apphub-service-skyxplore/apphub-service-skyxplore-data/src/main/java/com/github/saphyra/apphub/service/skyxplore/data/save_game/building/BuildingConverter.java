package com.github.saphyra.apphub.service.skyxplore.data.save_game.building;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class BuildingConverter extends ConverterBase<BuildingEntity, BuildingModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected BuildingModel processEntityConversion(BuildingEntity entity) {
        BuildingModel model = new BuildingModel();
        model.setId(uuidConverter.convertEntity(entity.getBuildingId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.BUILDING);
        model.setSurfaceId(uuidConverter.convertEntity(entity.getSurfaceId()));
        model.setDataId(entity.getDataId());
        model.setLevel(entity.getLevel());
        return model;
    }

    @Override
    protected BuildingEntity processDomainConversion(BuildingModel domain) {
        return BuildingEntity.builder()
            .buildingId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .surfaceId(uuidConverter.convertDomain(domain.getSurfaceId()))
            .dataId(domain.getDataId())
            .level(domain.getLevel())
            .build();
    }
}
