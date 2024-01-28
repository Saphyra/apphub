package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingData;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.util.HeadquartersUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
class ResourceRequirementProcessFactory {
    private final ProductionBuildingService productionBuildingService;
    private final ProductionRequirementsAllocationService productionRequirementsAllocationService;
    private final ProductionOrderProcessFactory productionOrderProcessFactory;
    private final HeadquartersUtil headquartersUtil;

    List<ProductionOrderProcess> createResourceRequirementProcesses(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, String dataId, int amount, String producerBuildingDataId) {
        return Optional.ofNullable(productionBuildingService.get(producerBuildingDataId))
            .map(ProductionBuildingData::getGives)
            .map(gives -> gives.get(dataId))
            .orElseGet(() -> headquartersUtil.getProductionData(dataId))
            .getConstructionRequirements()
            .getRequiredResources()
            .entrySet()
            .stream()
            .map(entry -> new BiWrapper<>(entry.getKey(), entry.getValue() * amount))
            .peek(biWrapper -> log.info("Required {} of {}", biWrapper.getEntity2(), biWrapper.getEntity1()))
            .map(entry -> productionRequirementsAllocationService.allocate(progressDiff, gameData, location, processId, entry.getEntity1(), entry.getEntity2()))
            .flatMap(reservedStorageId -> productionOrderProcessFactory.create(gameData, processId, location, reservedStorageId).stream())
            .collect(Collectors.toList());
    }
}
