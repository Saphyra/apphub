package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_module;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BuildingModuleConverter extends ConverterBase<BuildingModuleEntity, BuildingModuleModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected BuildingModuleEntity processDomainConversion(BuildingModuleModel domain) {
        return BuildingModuleEntity.builder()
            .buildingModuleId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .constructionAreaId(uuidConverter.convertDomain(domain.getConstructionAreaId()))
            .location(uuidConverter.convertDomain(domain.getLocation()))
            .dataId(domain.getDataId())
            .build();
    }

    @Override
    protected BuildingModuleModel processEntityConversion(BuildingModuleEntity entity) {
        BuildingModuleModel model = new BuildingModuleModel();
        model.setId(uuidConverter.convertEntity(entity.getBuildingModuleId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.BUILDING_MODULE);
        model.setConstructionAreaId(uuidConverter.convertEntity(entity.getConstructionAreaId()));
        model.setLocation(uuidConverter.convertEntity(entity.getLocation()));
        model.setDataId(entity.getDataId());
        return model;
    }
}
