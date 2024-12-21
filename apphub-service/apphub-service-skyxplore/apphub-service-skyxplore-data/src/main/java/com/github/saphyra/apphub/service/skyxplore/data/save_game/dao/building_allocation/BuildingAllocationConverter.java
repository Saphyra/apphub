package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingModuleAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingAllocationConverter extends ConverterBase<BuildingModuleAllocationEntity, BuildingModuleAllocationModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected BuildingModuleAllocationEntity processDomainConversion(BuildingModuleAllocationModel domain) {
        return BuildingModuleAllocationEntity.builder()
            .buildingAllocationId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .buildingModuleId(uuidConverter.convertDomain(domain.getBuildingModuleId()))
            .processId(uuidConverter.convertDomain(domain.getProcessId()))
            .build();
    }

    @Override
    protected BuildingModuleAllocationModel processEntityConversion(BuildingModuleAllocationEntity entity) {
        BuildingModuleAllocationModel model = new BuildingModuleAllocationModel();

        model.setId(uuidConverter.convertEntity(entity.getBuildingAllocationId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.CITIZEN_ALLOCATION);
        model.setBuildingModuleId(uuidConverter.convertEntity(entity.getBuildingModuleId()));
        model.setProcessId(uuidConverter.convertEntity(entity.getProcessId()));

        return model;
    }
}
