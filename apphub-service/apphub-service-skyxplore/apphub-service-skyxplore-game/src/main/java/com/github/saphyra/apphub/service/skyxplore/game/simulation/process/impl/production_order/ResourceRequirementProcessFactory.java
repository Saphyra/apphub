package com.github.saphyra.apphub.service.skyxplore.game.simulation.process.impl.production_order;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProducerBuildingModule;
import com.github.saphyra.apphub.lib.skyxplore.data.gamedata.building.production.ProductionBuildingService;
import com.github.saphyra.apphub.service.skyxplore.game.domain.GameProgressDiff;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
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

    List<ProductionOrderProcess> createResourceRequirementProcesses(GameProgressDiff progressDiff, GameData gameData, UUID processId, UUID location, String resourceDataId, int amount, String producerBuildingDataId) {
        return Optional.ofNullable(productionBuildingService.get(producerBuildingDataId))
            .map(ProducerBuildingModule::getProduces)
            .flatMap(productions -> productions.stream().filter(production -> production.getResourceDataId().equals(resourceDataId)).findAny())
            .orElseThrow(() -> ExceptionFactory.reportedException(producerBuildingDataId + " cannot produce " + resourceDataId))
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
