package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.service.skyxplore.game.domain.data.GameData;
import com.github.saphyra.apphub.service.skyxplore.game.domain.data.construction.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcess;
import com.github.saphyra.apphub.service.skyxplore.game.process.impl.production_order.ProductionOrderProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductionOrderProcessFactoryForConstruction {
    private final ProductionOrderProcessFactory productionOrderProcessFactory;

    public List<ProductionOrderProcess> createProductionOrderProcesses(UUID processId, GameData gameData, UUID location, Construction construction) {
        log.info("Creating ProductionOrderProcesses...");

        return gameData.getReservedStorages()
            .getByExternalReference(construction.getConstructionId())
            .stream()
            .flatMap(reservedStorage -> productionOrderProcessFactory.create(gameData, processId, location, reservedStorage.getReservedStorageId()).stream())
            .collect(Collectors.toList());
    }
}
