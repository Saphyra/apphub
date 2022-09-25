package com.github.saphyra.apphub.service.skyxplore.game.process.impl;

import com.github.saphyra.apphub.service.skyxplore.game.domain.Game;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Construction;
import com.github.saphyra.apphub.service.skyxplore.game.domain.map.Planet;
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

    public List<ProductionOrderProcess> createProductionOrderProcesses(UUID processId, Game game, Planet planet, Construction construction) {
        log.info("Creating ProductionOrderProcesses...");

        return planet.getStorageDetails()
            .getReservedStorages()
            .stream()
            .filter(reservedStorage -> reservedStorage.getExternalReference().equals(construction.getConstructionId()))
            .flatMap(reservedStorage -> productionOrderProcessFactory.create(processId, game, planet, reservedStorage.getReservedStorageId()).stream())
            .collect(Collectors.toList());
    }
}
