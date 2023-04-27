package com.github.saphyra.apphub.service.skyxplore.data.save_game.dao.building_allocation;

import com.github.saphyra.apphub.api.skyxplore.model.game.BuildingAllocationModel;
import com.github.saphyra.apphub.api.skyxplore.model.game.GameItemType;
import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class BuildingAllocationConverter extends ConverterBase<BuildingAllocationEntity, BuildingAllocationModel> {
    private final UuidConverter uuidConverter;

    @Override
    protected BuildingAllocationEntity processDomainConversion(BuildingAllocationModel domain) {
        return BuildingAllocationEntity.builder()
            .buildingAllocationId(uuidConverter.convertDomain(domain.getId()))
            .gameId(uuidConverter.convertDomain(domain.getGameId()))
            .buildingId(uuidConverter.convertDomain(domain.getBuildingId()))
            .processId(uuidConverter.convertDomain(domain.getProcessId()))
            .build();
    }

    @Override
    protected BuildingAllocationModel processEntityConversion(BuildingAllocationEntity entity) {
        BuildingAllocationModel model = new BuildingAllocationModel();

        model.setId(uuidConverter.convertEntity(entity.getBuildingAllocationId()));
        model.setGameId(uuidConverter.convertEntity(entity.getGameId()));
        model.setType(GameItemType.CITIZEN_ALLOCATION);
        model.setBuildingId(uuidConverter.convertEntity(entity.getBuildingId()));
        model.setProcessId(uuidConverter.convertEntity(entity.getProcessId()));

        return model;
    }
}
